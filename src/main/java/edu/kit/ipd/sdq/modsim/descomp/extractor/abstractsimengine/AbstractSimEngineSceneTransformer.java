package edu.kit.ipd.sdq.modsim.descomp.extractor.abstractsimengine;

import java.util.*;

import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Attribute;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Entity;
import edu.kit.ipd.sdq.modsim.descomp.data.simulator.Event;
import soot.*;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.pointer.RWSet;
import soot.jimple.toolkits.pointer.SideEffectAnalysis;
import soot.util.Chain;

public class AbstractSimEngineSceneTransformer extends SceneTransformer {

    private static final String EVENT_CLASS = "de.uka.ipd.sdq.simulation.abstractsimengine.AbstractSimEventDelegator";
    private static final String ENTITY_CLASS = "de.uka.ipd.sdq.simulation.abstractsimengine.AbstractSimEntityDelegator";

	private List<Event> events = new ArrayList<Event>();
	private List<Entity> entities = new ArrayList<Entity>();

	public AbstractSimEngineSceneTransformer() {
		events = new ArrayList<Event>();
		entities = new ArrayList<Entity>();
	}

	@Override
	protected void internalTransform(String phaseName, Map options) {
		CHATransformer.v().transform();
		//CallGraph cg = Scene.v().getCallGraph();
		//
		//System.out.println(cg.size());
		//
		//Iterator<MethodOrMethodContext> sourceMethods = cg.sourceMethods();


		Hierarchy classHierarchy = Scene.v().getActiveHierarchy();

		extractEvents(events, classHierarchy);
		extractEntities(entities, classHierarchy);

	}

	private void extractEntities(List<Entity> entities, Hierarchy classHierarchy) {
		SootClass entityParentClass = Scene.v().getSootClass(ENTITY_CLASS);
//		 List<SootClass> entityList = classHierarchy.getDirectSubclassesOf(entityParentClass);

		List<SootClass> entityList = classHierarchy.getSubclassesOf(entityParentClass);

		for (SootClass entity : entityList) {
			entity.setApplicationClass();
			System.out.println(entity.getJavaStyleName());

			Entity currEntity = new Entity(entity.getShortName());
			Chain<SootField> fields = entity.getFields();
			fields.forEach(
					field -> currEntity.addAttribute(new Attribute(field.getName(), field.getType().toString())));

			entities.add(currEntity);
		}
	}

	private void extractEvents(List<Event> events, Hierarchy classHierarchy) {
        SootClass eventClass = Scene.v().getSootClass(EVENT_CLASS);
        List<SootClass> eventList = classHierarchy.getSubclassesOf(eventClass);

        Map<String, Event> eventCache = new HashMap<String, Event>();


        for (SootClass event : eventList) {
			event.setApplicationClass();
			SideEffectAnalysis sideEffectAnalysis = Scene.v().getSideEffectAnalysis();

			for (SootMethod sootMethod : event.getMethods()) {

				if (sootMethod.getSignature().contains("eventRoutine")) {

					Body retrieveActiveBody = sootMethod.retrieveActiveBody();

					Event currEvent = new Event(event.getJavaStyleName());
					if (eventCache.containsKey(event.getJavaStyleName())) {
						currEvent = eventCache.get(event.getJavaStyleName());
					} else {
						eventCache.put(event.getJavaStyleName(), currEvent);
					}

					System.out.println(event.getJavaStyleName());
					for (Unit unit : retrieveActiveBody.getUnits()) {

						if (unit instanceof JInvokeStmt) {

							ValueBox invokeExprBox = ((JInvokeStmt) unit).getInvokeExprBox();
							Value value = invokeExprBox.getValue();

							if (value instanceof JVirtualInvokeExpr) {
								value = (JVirtualInvokeExpr) value;
								SootMethodRef methodRef = ((JVirtualInvokeExpr) value).getMethodRef();

								if (eventList.contains(methodRef.getDeclaringClass())) {

									if (methodRef.getName().startsWith("schedule")) {
										Event event2 = new Event(methodRef.getDeclaringClass().getShortName());

										if (eventCache.containsKey(event2.getName())) {
											event2 = eventCache.get(event2.getName());
										} else {
											eventCache.put(event2.getName(), event2);
										}

										currEvent.addSchedulesEvent(event2, "", "");
									}
								}

							}
						}
					}

					RWSet nonTransitiveReadSet = sideEffectAnalysis.nonTransitiveReadSet(sootMethod);
					RWSet nonTransitiveWriteSet = sideEffectAnalysis.nonTransitiveWriteSet(sootMethod);

					if (null != nonTransitiveReadSet) {
						for (Object string : nonTransitiveReadSet.getGlobals()) {

							SootField field = (SootField) string;
							currEvent.addReadAttribute(new Attribute(field.getName(), field.getType().toString()));
						}

						for (Object object : nonTransitiveReadSet.getFields()) {
							System.out.println(object);

							if (object instanceof SootField) {
								SootField field = (SootField) object;
								currEvent.addReadAttribute(new Attribute(field.getName(), field.getType().toString()));
							}
						}
					}

					if (null != nonTransitiveWriteSet) {
						for (Object string : nonTransitiveWriteSet.getGlobals()) {

							currEvent.addWriteAttribute(new Attribute(((SootField) string).getName(),
									((SootField) string).getType().toString()), "", "");
						}

						for (Object object : nonTransitiveWriteSet.getFields()) {
							SootField field = (SootField) object;

							currEvent.addWriteAttribute(new Attribute(field.getName(), field.getType().toString()), "",
									"");
						}
					}

					events.add(currEvent);
				}

			}

		}
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
}
