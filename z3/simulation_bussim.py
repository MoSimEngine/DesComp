from z3 import *

#ArriveEvent
# Change State of Bus -> BusState.ARRIVED; Position = Desitnation; Destination = null
# Schedule Event UnloadPassengersEvent in 0 tu (timeunits)

#Enity Bus
class Bus():
    UNLOADING_TIME_PER_PASSENGER = 10

    def __init__(self, state, occupiedSeats, position, desitination, totalSeats):
        self.state = state
        self.occupiedSeats = occupiedSeats
        self.position = position
        self.destination = desitination
        self.totalSeats = totalSeats


class BusStop():
    def __init__(self, id, waitingPassengers):
      self.id = id
      self.waitingPassengers = waitingPassengers


BusState = Datatype('BusState')
BusState.declare('LOADING_PASSENGERS')
BusState.declare('TRAVELLING')
BusState.declare('ARRIVED')
BusState.declare('UNLOADING_PASSENGERS')
BusState = BusState.create()


totalSeats = Int('totalSeats')
busStopId = Int('busStopId')
waitingPassengers = Int('waitingPassengers')
occupiedSeats = Int('occupiedSeats')

busStop = BusStop(busStopId, waitingPassengers)

#Effect on Bus
ArriveEvent_bus = Bus(BusState.ARRIVED, occupiedSeats,
                      busStop, None, totalSeats)

#Event specification
#Function to compute delay
ArriveEvent_delay = 0


#UnloadPassengersEvent
# Change State of Bus

utotalSeats = Int('utotalSeats')
ubusStopId = Int('ubusStopId')
uwaitingPassengers = Int('uwaitingPassengers')
uoccupiedSeats = Int('uoccupiedSeats')
ubusStop = BusStop(busStopId, waitingPassengers)
UnloadPassengersEvent_bus = Bus(
    BusState.UNLOADING_PASSENGERS, uoccupiedSeats, ubusStop, ubusStop, utotalSeats)

UnloadPassengersEvent_delay = uoccupiedSeats * \
    UnloadPassengersEvent_bus.UNLOADING_TIME_PER_PASSENGER






#UnloadingFinishedEvent
#No Effects
#Schedule next Event 0 -> LoadPassengersEvent
UnloadingFinishedEvent_delay = 0

#LoadPassengersEvent
#Change State of Bus to LOADING_PASSENGERS and change number of occupiedSeats
#Schedule LoadFinishedEvent

LOADING_TIME_PER_PASSENGER = 3
lbusStopId = Int('lbusStopId')
lwaitingPassengers = Int('lwaitingPassengers')
loccupiedSeats = Int('loccupiedSeats')
ltotalSeats = Int('ltotalSeats')
lbusStop = BusStop(lbusStopId, lwaitingPassengers)
LoadPassengersEvent_bus = Bus(
    BusState.LOADING_PASSENGERS, loccupiedSeats, lbusStop, None, ltotalSeats)
servedPassengers = If(lbusStop.waitingPassengers < LoadPassengersEvent_bus.totalSeats,
                      lbusStop.waitingPassengers, LoadPassengersEvent_bus.totalSeats)



LoadPassengersEvent_bus.occupiedSeats = servedPassengers

lbusStop.waitingPassengers = waitingPassengers - servedPassengers

LoadPassengersEvent_delay = servedPassengers * LOADING_TIME_PER_PASSENGER


#LoadFinishedEvent
#Schedule next Event Travel Event

LoadFinishedEvent = 0


#TravelEvent
#Change bus.state; bus.destination; bus.position




class RouteSegment():
    def __init__(self, fromBusStop, toBusStop, distance, averageSpeed):
        self.fromBusStop = fromBusStop
        self.toBusStop = toBusStop
        self.distance = distance
        self.averageSpeed = averageSpeed




LOADING_TIME_PER_PASSENGER = 3
tbusStopId = Int('tbusStopId')
twaitingPassengers = Int('twaitingPassengers')
toccupiedSeats = Int('toccupiedSeats')
ttotalSeats = Int('ttotalSeats')
tbusStop = BusStop(tbusStopId, twaitingPassengers)
TravelEvent_bus = Bus(
    BusState.TRAVELLING, toccupiedSeats, tbusStop, tbusStop, ttotalSeats)


distance = Int('distance')
averageSpeed = Int('averageSpeed')
route = RouteSegment(None, tbusStop, distance,averageSpeed)
TravelEvent_bus.state = BusState.TRAVELLING
TravelEvent_bus.destination = route.toBusStop

TravelEvent_delay = (distance / averageSpeed)*60*60


print ('################################### Check Behavior ###################################')


def compareBehavior(object1, object2):
  for attr, value in object1.__dict__.items():
        print("Type: {} Attribute: {} Function: {}".format(type(object1).__name__, attr, value))
        print("Type: {} Attribute: {} Function: {}".format(type(object2).__name__, attr, object2.__dict__[attr]))

        print("Is it possible that Event {} and Event {} change Property {} in the same way?".format(
            type(object1).__name__, type(object2).__name__, attr))
        solve(value == object2.__dict__[attr])

        try:
          print("Do the Event {} and Event {} change Property {} every time in the same way?".format(
              type(object1).__name__, type(object2).__name__, attr))
          prove(value == object2.__dict__[attr])
        except:
          print("Error occured during proving")

effectsOnBus =	[ArriveEvent_bus, UnloadPassengersEvent_bus, LoadPassengersEvent_bus]

for busA in effectsOnBus:
  for busB in effectsOnBus:
    compareBehavior(busA, busB)

print ('################################### Check Delays ###################################')

delays =	{
  "ArriveEvent": ArriveEvent_delay,
  "UnloadPassengersEvent": UnloadPassengersEvent_delay,
  "UnloadingFinishedEvent" : UnloadingFinishedEvent_delay,
  "LoadPassengersEvent": LoadPassengersEvent_delay,
  "LoadFinishedEvent" : LoadFinishedEvent,
  "TravelEvent": TravelEvent_delay
}

print("EventA: {} Function: {}".format("UnloadingFinishedEvent", UnloadPassengersEvent_delay))
print("EventB: {} Function: {}".format("LoadPassengersEvent", LoadPassengersEvent_delay))
#print("When is it possible that {} and {} schedule their follow up Event at the same time?".format(eventA , eventB))
prove(UnloadPassengersEvent_delay == LoadPassengersEvent_delay)


#for eventA, delayFunctionA in delays.items():
#  for eventB, delayFunctionB in delays.items():
#    print("EventA: {} Function: {}".format(eventA, delayFunctionA))
#    print("EventB: {} Function: {}".format(eventB, delayFunctionB))
#    print("When is it possible that {} and {} schedule their follow up Event at the same time?".format(eventA , eventB))
#    solve(delayFunctionA == delayFunctionB)


