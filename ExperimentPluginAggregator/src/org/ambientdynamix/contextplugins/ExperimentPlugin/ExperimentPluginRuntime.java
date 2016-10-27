package org.ambientdynamix.contextplugins.ExperimentPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.ambientdynamix.api.contextplugin.ContextPluginSettings;
import org.ambientdynamix.api.contextplugin.PowerScheme;
import org.ambientdynamix.api.contextplugin.ReactiveContextPluginRuntime;
import org.ambientdynamix.api.contextplugin.security.PrivacyRiskLevel;
import org.ambientdynamix.api.contextplugin.security.SecuredContextInfo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import eu.smartsantander.androidExperimentation.jsonEntities.Reading;

public class ExperimentPluginRuntime extends ReactiveContextPluginRuntime {

	private final String TAG = this.getClass().getSimpleName();
	private static final String NAME = "ExperimentAggregator";
	private Context context;
	private Reading gpsReading;
	private Reading noiseReading;
	private Bundle b;

	@Override
	public void init(PowerScheme powerScheme, ContextPluginSettings settings)
			throws Exception {
		this.setPowerScheme(powerScheme);
		this.context = this.getSecuredContext();
		b = new Bundle();
		Log.w(TAG, NAME + " Inited!");
	}

	// handle incoming context request
	@Override
	public void handleContextRequest(UUID requestId, String contextType) {
		Log.w(TAG, NAME + " Workload Started!");
		try {

			List<Reading> readings = new ArrayList<Reading>();
			
			for (String key : b.keySet()){
				Log.i(NAME, key);
				if (key.equals("org.ambientdynamix.contextplugins.ExperimentPlugin")){
					continue;
				}
				String jsonReading = b.getString(key);
				Reading reading = Reading.fromJson(jsonReading);
				readings.add(new Reading(Reading.Datatype.String, reading.toJson(), PluginInfo.CONTEXT_TYPE));
			}
			
			final PluginInfo info = new PluginInfo();
			info.setState("ACTIVE");
			info.setPayload(readings);
			sendContextEvent(requestId, new SecuredContextInfo(info, PrivacyRiskLevel.LOW), 60000);
		} catch (Exception e) {
			Log.w(NAME + " Workload Error", e.toString());
		}
	}

	@Override
	public void handleConfiguredContextRequest(UUID requestId,
			String contextType, Bundle config) {
		b.clear();
		b.putAll(config);
		handleContextRequest(requestId, contextType);
	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}

	@Override
	public void destroy() {
		/*
		 * At this point, the plug-in should stop and release any resources.
		 * Nothing to do in this case except for stop.
		 */
		this.stop();
		Log.d(TAG, NAME + " Destroyed!");
	}

	@Override
	public void updateSettings(ContextPluginSettings settings) {
		// Not supported
	}

	@Override
	public void setPowerScheme(PowerScheme scheme) {
		// Not supported
	}

}