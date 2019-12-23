package ApplicationOperation;

import static org.junit.Assert.*;

import org.junit.Test;

import launcher.ApplicationModelSettings;
import launcher.ApplicationModelSettings.SupportedTopologyTypes;
import networkInitializer.NetworkSettingsBase;
import networkInitializer.baPreferentialAttachment.NetworkSettingsBaPreferentialAttachment;
import networkInitializer.gridStructured.NetworkSettingsGrid;
import networkInitializer.smallWorldKleinberg.NetworkSettingsSmallWorldKleinberg;
import ui.AppWindowActions;

public class ApplicationSettingsInitialization {

	@Test
	public void ShouldInitializeNetwork() {
		ApplicationModelSettings modelSettings = AppWindowActions.CreateInitialModelSettings();
		
		assertTrue(modelSettings.NetworkFacade.GetPeers().size() > 50);
		
		
		NetworkSettingsBase settings  = modelSettings.GetSettingsByType(SupportedTopologyTypes.PreferentialAttachment);
		NetworkSettingsBase settingsGrid  = modelSettings.GetSettingsByType(SupportedTopologyTypes.Grid);
		NetworkSettingsBase settingsSmallWorld  = modelSettings.GetSettingsByType(SupportedTopologyTypes.SmallWorld);
		assertTrue(settings instanceof NetworkSettingsBaPreferentialAttachment);
		assertTrue(settingsGrid instanceof NetworkSettingsGrid);
		assertTrue(settingsSmallWorld instanceof NetworkSettingsSmallWorldKleinberg);
	}

}
