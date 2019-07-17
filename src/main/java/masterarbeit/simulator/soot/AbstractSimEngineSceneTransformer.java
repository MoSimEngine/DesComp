package masterarbeit.simulator.soot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import masterarbeit.simulator.Attribute;
import masterarbeit.simulator.Entity;
import masterarbeit.simulator.Event;
import masterarbeit.simulator.Property;
import soot.Body;
import soot.Hierarchy;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.toolkits.callgraph.CHATransformer;
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
//		CallGraph cg = Scene.v().getCallGraph();
//
//		System.out.println(cg.size());
//
//		Iterator<MethodOrMethodContext> sourceMethods = cg.sourceMethods();

		Hierarchy classHierarchy = Scene.v().getActiveHierarchy();

		extractEvents(events, classHierarchy);
		extractEntities(entities, classHierarchy);

	}

	private void extractEntities(List<Entity> entities, Hierarchy classHierarchy) {
		SootClass entityParentClass = Scene.v().getSootClass(ENTITY_CLASS);
		List<SootClass> entityList = classHierarchy.getDirectSubclassesOf(entityParentClass);

		for (SootClass entity : entityList) {
			entity.setApplicationClass();

			Entity currEntity = new Entity(entity.getShortName());
			Chain<SootField> fields = entity.getFields();
			fields.forEach(
					field -> currEntity.addWriteAttribute(new Attribute(field.getName(), field.getType().toString())));

			entities.add(currEntity);
		}
	}

	private void extractEvents(List<Event> events, Hierarchy classHierarchy) {
		SootClass eventClass = Scene.v().getSootClass(EVENT_CLASS);
		List<SootClass> eventList = classHierarchy.getSubclassesOf(eventClass);

		for (SootClass event : eventList) {
			event.setApplicationClass();
			SideEffectAnalysis sideEffectAnalysis = Scene.v().getSideEffectAnalysis();

			for (SootMethod sootMethod : event.getMethods()) {

				if (sootMethod.getSignature().contains("eventRoutine")) {

					Body retrieveActiveBody = sootMethod.retrieveActiveBody();

					Event currEvent = new Event(event.getJavaStyleName(), retrieveActiveBody.toString());

					for (Unit unit : retrieveActiveBody.getUnits()) {

						if (unit instanceof JInvokeStmt) {

							ValueBox invokeExprBox = ((JInvokeStmt) unit).getInvokeExprBox();
							Value value = invokeExprBox.getValue();

							if (value instanceof JVirtualInvokeExpr) {
								value = (JVirtualInvokeExpr) value;
								SootMethodRef methodRef = ((JVirtualInvokeExpr) value).getMethodRef();

								if (eventList.contains(methodRef.getDeclaringClass())) {

									if (methodRef.getName().startsWith("schedule")) {
										currEvent.addSchedulesEvent(
												new Event(methodRef.getDeclaringClass().getShortName(), null));
									}
								}

							}
						}
					}

					RWSet nonTransitiveReadSet = sideEffectAnalysis.nonTransitiveReadSet(sootMethod);
					RWSet nonTransitiveWriteSet = sideEffectAnalysis.nonTransitiveWriteSet(sootMethod);

					if (null != nonTransitiveReadSet) {
						for (Object string : nonTransitiveReadSet.getGlobals()) {
							currEvent.addReadProperty(new Property(((SootField) string).getName()));
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
							currEvent.addWriteProperty(new Property(((SootField) string).getName()));
						}

						for (Object object : nonTransitiveWriteSet.getFields()) {
							SootField field = (SootField) object;
							currEvent.addWriteAttribute(new Attribute(field.getName(), field.getType().toString()));
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
