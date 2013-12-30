package es.bq.bqapp;

/*
 * Esta clase es un singletone para compartir la indformacion de forma eficiente entre los activities
 */
public enum EnumHolderData {
	INSTANCE;
	private Object mObject;

	public static boolean hasData() {
		return INSTANCE.mObject != null;
	}

	public static void setData(final Object objectList) {
		INSTANCE.mObject = objectList;
	}

	public static Object getData() {
		final Object retList = INSTANCE.mObject;
		INSTANCE.mObject= null;
		return retList;
	}
}