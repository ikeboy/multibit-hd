package org.multibit.hd.ui.views.wizards.sound_settings;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.ComboBoxes;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Sound settings: Enter details</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */

public class SoundSettingsPanelView extends AbstractWizardPanelView<SoundSettingsWizardModel, SoundSettingsPanelModel> implements ActionListener {

  // Panel specific components
  private JComboBox<String> alertSoundYesNoComboBox;
  private JComboBox<String> receiveSoundYesNoComboBox;
  private Configuration viewConfiguration;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name
   */
  public SoundSettingsPanelView(AbstractWizard<SoundSettingsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.SHOW_SOUNDS_WIZARD, AwesomeIcon.MUSIC);

  }

  @Override
  public void newPanelModel() {

    // Use a deep copy to avoid reference leaks
    viewConfiguration = Configurations.currentConfiguration.deepCopy();

    // Configure the panel model
    setPanelModel(new SoundSettingsPanelModel(
      getPanelName(),
      viewConfiguration
    ));

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][][]", // Column constraints
      "[][][]" // Row constraints
    ));

    alertSoundYesNoComboBox = ComboBoxes.newAlertSoundYesNoComboBox(this, viewConfiguration.getSound().isAlertSound());
    receiveSoundYesNoComboBox = ComboBoxes.newReceiveSoundYesNoComboBox(this, viewConfiguration.getSound().isReceiveSound());

    JButton playBeep = Buttons.newPlaySoundButton(getPlayBeepAction(), MessageKey.PLAY_SOUND, MessageKey.PLAY_SOUND_TOOLTIP);
    JButton playPaymentReceived = Buttons.newPlaySoundButton(getPlayPaymentReceivedAction(), MessageKey.PLAY_SOUND, MessageKey.PLAY_SOUND_TOOLTIP);

    contentPanel.add(Labels.newSoundChangeNote(), "growx,span 3,wrap");

    contentPanel.add(Labels.newSelectAlertSound(), "shrink");
    contentPanel.add(alertSoundYesNoComboBox, "growx");
    contentPanel.add(playBeep, "shrink,wrap");

    contentPanel.add(Labels.newSelectReceiveSound(), "shrink");
    contentPanel.add(receiveSoundYesNoComboBox, "growx");
    contentPanel.add(playPaymentReceived, "shrink,wrap");

  }

  private Action getPlayPaymentReceivedAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Sounds.playPaymentReceived();

      }
    };
  }

  private Action getPlayBeepAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Sounds.playBeep();

      }
    };
  }

  @Override
  protected void initialiseButtons(AbstractWizard<SoundSettingsWizardModel> wizard) {

    PanelDecorator.addCancelApply(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Apply button starts off enabled
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.APPLY, true);

  }

  @Override
  public void afterShow() {

    alertSoundYesNoComboBox.requestFocusInWindow();

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    if (!isExitCancel) {

      // Switch the main configuration over to the new one
      Configurations.switchConfiguration(getWizardModel().getConfiguration());

    }

    // Must be OK to proceed
    return true;

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {


  }


  /**
   * <p>Handle one of the combo boxes changing</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    // Create a new configuration
    JComboBox source = (JComboBox) e.getSource();
    if (ComboBoxes.ALERT_SOUND_COMMAND.equals(e.getActionCommand())) {
      viewConfiguration.getSound().setAlertSound(source.getSelectedIndex() == 0);
    }
    if (ComboBoxes.RECEIVE_SOUND_COMMAND.equals(e.getActionCommand())) {
      viewConfiguration.getSound().setReceiveSound(source.getSelectedIndex() == 0);
    }

    // Update the model

    getWizardModel().setConfiguration(viewConfiguration);

  }

}
