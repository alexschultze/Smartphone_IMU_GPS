#! /usr/bin/python

## Receive and process data messages from "Sensorstream IMU+GPS" app
## by Axel Lorenz
## https://play.google.com/store/apps/
##              details?id=de.lorenz_fenster.sensorstreamgps
## The app shows up on the phone as "IMU+GPS Stream"

## The app appears to be a superset of the "Wireless IMU" app
## from Jan Zwiener
## https://play.google.com/store/apps/details?id=org.zwiener.wimu

## Write data to stdout in .csv format, properly aligned in columns
##
## In all likelihood, not all sensors are reported in any given
## message.  Fields without data are filled with the string "x".

## Configure the /interest/ list to specify which sensors you are
## interested in.

## Note on axes: The accelerometer uses /body axes/ (not lab-frame
## axes or anything like that).  If you hold the phone vertically so
## that the name is on top, facing the text on the screen, then:
##   -- The +X axis is your left-to-right direction, i.e.  the
##      direction in which a line of text is written on the screen
##   -- The +Y direction is toward the top, i.e. the opposite of
##      the direction in which successive lines are written.
##   -- The +Z direction is out of the screen toward you.  This
##      creates a right-hand system {X, Y, Z}.

## There's a picture (and other information) here:
## http://developer.android.com/guide/topics/sensors/sensors_overview.html

## Accelerations are relative to a local freely-falling frame, (not
## the lab frame), in accordance with a modern (post-1915)
## understanding of physics.  In particular, a phone at rest in the
## lab frame is being accelerated skyward at a rate of roughly 9.8
## m/s/s.


from __future__ import print_function

######################################################################
## configuration section
syntax = {
  1:  ['gps', 'lat', 'lon', 'alt'],     # deg, deg, meters MSL WGS84
  3:  ['accel', 'x', 'y', 'z'],         # m/s/s
  4:  ['gyro', 'x', 'y', 'z'],          # rad/s
  5:  ['mag', 'x', 'y', 'z'],           # microTesla
  6:  ['gpscart', 'x', 'y', 'z'],       # (Cartesian XYZ) meters
  7:  ['gpsv', 'x', 'y', 'z'],          # m/s
  8:  ['gpstime', ''],                  # ms
  81: ['orientation', 'x', 'y', 'z'],   # degrees
  82: ['lin_acc',     'x', 'y', 'z'],
  83: ['gravity',     'x', 'y', 'z'],   # m/s/s
  84: ['rotation',    'x', 'y', 'z'],   # radians
  85: ['pressure',    ''],              # ???
  86: ['battemp', ''],                  # centigrade

# Not exactly sensors, but still useful data channels:
 -10: ['systime', ''],
 -11: ['from', 'IP', 'port'],
}

interest = [-11, -10, 8,1,3,4,5,6,7,86]

# Sensor id:
# 1     - GPS lat, lon, elev
# 3     - Accelerometer (m/s^2)
# 4     - Gyroscope     (rad/s)
# 5     - Magnetometer  (microTesla)
# 6     - GPS??
# 7     - GPS??
# 8     - GPS time      (ms)
# 86    - battery temperature

# End configuration
######################################################################

import socket, traceback, sys, errno

verbosity = 0
rawmode = 0
for arg in sys.argv[1:]:
  if arg == '-v':
     verbosity += 1
  elif arg == '-r':
     rawmode += 1
  else:
     print("Unrecognized option:", arg, file=sys.stderr)
     exit(0)

revint = {}

nextchan = 0
count = 0
bases = []
for key in interest:
  revint[key] = count
  bases.append(nextchan)
  info = syntax[key] [:]
  name = info.pop(0)
  if verbosity:
      print(key, "*** Name:", name, len(info))
  nextchan += len(info)
  count += 1

# nextchan is now one past the end of the row
# i.e. nextchan = the total number of data columns
if verbosity:
    print('bases:', bases, nextchan)
    print('revint:', revint)

if verbosity:
    sys.stdout.flush()

if 1:   # print the column headers
  print('"hdr:"', end='')
  for key in interest:
    info = syntax[key] [:]
    name = info.pop(0)
    for component in info:
      subname = name
      if component != '':
        subname += '.' + component
      print(', "' + subname + '"', end='')
  print()
  sys.stdout.flush()

hostname = ''           # accept connections to any alias of this host
port = 5555

# Allocate a datagram (UDP) socket:
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

# This option is more important for TCP,
# but harmless here:
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

##xx not needed:
##xx s.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)

# start using the socket:
s.bind((hostname, port))

while 1:                # loop over all received messages
    try:
        hackflag = 0
        rawmessage, (peerIP, peerport) = s.recvfrom(8192)
# add fake data to front of message:
        message = '-11, ' +  peerIP + ', '       \
                         + ('%d' % peerport) + ', ' \
                         + '-10, ' + rawmessage
        parse = message.split(',')      # split data into words
# The marker string gets modified in the case of
# multi-sensor messages
        marker = '" msg:", '

        channels = ['"x"'] * nextchan
#xx        channels[1] = 'Y'
#xx        print(channels)
        if rawmode == 1:                     # print the raw message
            print(marker, rawmessage)
        if rawmode == 2:
            print(marker, message)

# loop over all sensors mentioned in this message:
        while len(parse):
            ID = parse.pop(0)
            ID = int(float(ID))
            if ID == 8:
              hackflag = 1
            sys.stdout.flush()
### FIXME: unexpected ID
            if not(ID in syntax):
              print('No idea what to do with ID:', ID)
              print('Please add it to the syntax dictionary.')
              print(rawmessage)
              break            # give up on the rest of this message

            info = syntax[ID] [:]
            name = info.pop(0);

            dim = len(info)
            reading = parse[0:dim]
            parse = parse[dim:]
# parse has been updated.
# If we want to skip ahead, it is now safe to do so.

            if not ID in revint:
              if verbosity:
                print('FWIW ID:', ID, name, "is not interesting.")
              continue

            index = revint[ID]
            base = bases[index]

# deposit data from this sensor into the appropriate channels:
            channels[base:base+dim] = reading[:]

# All data has been collected from this message.
# Convert array of strings to one long string:
        str = ', '.join(channels)

        marker = '"data:"'
# so we can easily tell which lines have GPS data:
        if hackflag: marker = '"data@:"'
        print(marker, ', ',
          str, sep='')
        sys.stdout.flush()

    except (KeyboardInterrupt, SystemExit):
        raise
    except IOError, e:
        if e.errno == errno.EPIPE:
            exit(0)
    except:
        traceback.print_exc()
# -------------------------------------------------------