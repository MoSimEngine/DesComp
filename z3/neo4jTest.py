from z3 import *
from neo4j import GraphDatabase
import array as arr


class Event:
  def __init__(self, name, formula):
    self.name = name
    self.formula = formula


def check_equality(event, events):

    for e1 in events:
        #print(event.formula + " " + e1.formula)
        prove(event.formula == 0)
#  '   x = Int('x')
#     y = Int('y')
# prove(Select(Store(A,x,1),0) == Select(Store(A,x,0),0))

# s = Solver()
# s.add(Select(Store(A,x,1),0) == Select(Store(A,x,0),0))
# print(s.check())
# print(s.model())
# print ("Test equal write")'

# driver = GraphDatabase.driver("bolt://35.193.233.92:7687", auth=("neo4j", "ZtA6tdah1FxB"))

# def print_events(tx):
#     for record in tx.run("MATCH (n:Event)-[:SCHEDULES]->(scheduledEvent) RETURN n.name, scheduledEvent.name"):
#         print(record["n.name"] + record["r.schedulesFormula"] + record["scheduledEvent.name"])

# with driver.session() as session:
#     session.read_transaction(print_events)
#     driver.close()


#Arrive Event
# e.schedule(bus, 0);

e1 = Event("ArriveEvent", 0)

#LoadFinishedEvent
# e.schedule(bus, 0);

e2 = Event("LoadFinishedEvent", 0)

#LoadPassengersEvent
# int servedPassengers = Math.min(waitingPassengers, bus.getTotalSeats());
#for (int i = 0; i < servedPassengers; i++) {
#if (bus.containsDestinationInRoute((BusStop) h.getDestination())) {
#totalLoadingTime += loadingTime;
#e.schedule(bus, totalLoadingTime);

e3 = Event("LoadPassengersEvent", 0)


suitablePassengers = BitVecVal('x', 16)
waitingPassengers = Int('waitingPassengers')
totalSeats = Int('totalSeats')
loadingTime = Int('loadingTime')

servedPassengers = If(totalSeats < waitingPassengers
totalLoadingTime = servedPassengers * loadingTime

print(simplify(totalLoadingTime))


#TravelEvent
#double drivingTime = Duration.hours(segment.getDistance() / (double) segment.getAverageSpeed()).toSeconds().value();
#e.schedule(bus, drivingTime);

e4 = Event("TravelEvent", "0")

#UnloadingFinishedEvent
#e.schedule(bus, 0);

e5 = Event("UnloadingFinishedEvent", "0")

#UnloadPassengersEvent
# for(int i = 0; i < transportedHumans; i++){
#        	Human h = bus.unloadHuman();
#        	//System.out.println(h.getDestination().getName() + ":"  + bus.getPosition().getName());
#        	if(h.getDestination().equals(bus.getPosition())){
#
#//        		Utils.log(bus, "Unloading " + h.getName() + " at position " + position.getName());
#        		totalUnloadingTime += unloadingTime;
#double unloadingTime = Bus.UNLOADING_TIME_PER_PASSENGER.toSeconds().value();


#e.schedule(bus, totalUnloadingTime);

unloadingTime = Int('unloadingTime')
transportedHumans = Int('transportedHumans')

totalUnloadingTime = unloadingTime * transportedHumans
print(simplify(totalUnloadingTime))

s = Solver()
s.add(totalLoadingTime == totalUnloadingTime)
print(s.check())
print(s.model())

prove(totalLoadingTime == totalUnloadingTime)

e6 = Event("UnloadingFinishedEvent", "0")


events = [e1, e2, e3, e4, e5, e6]


print("Check Events")

#for event in events:
#check_equality(event, events)
