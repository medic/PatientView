package net.frontlinesms.plugins.patientview.ui.detailview;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.plugins.patientview.ui.ViewHandler;
import net.frontlinesms.ui.UiGeneratorController;

/**
 * An interface that all Detail View panels should implement
 * It provides structure so that the proper panel can be placed in the Detail View
 * when the related entity is selected in the main search screen.
 * 
 * Additionally, the panel will be notified before it appears and disappears so that it
 * can do any loading/unloading necessary
 * @author Dieterich
 *
 * @param <E> The class that this panel is linked to (This panel will be shown when this class is selected).
 */
public abstract class DetailViewPanelController<E> extends ViewHandler{

	public DetailViewPanelController(UiGeneratorController ui, ApplicationContext appCon) {
		super(ui, appCon);
	}
	
	public DetailViewPanelController(UiGeneratorController ui, ApplicationContext appCon, String xmlFilePath) {
		super(ui, appCon,xmlFilePath);
	}

	/**
	 * @return The class that this panel is linked to
	 */
	public abstract Class<E> getEntityClass();
	
	/**
	 * Method that notifies the controller before it appears
	 * @param entity The entity that has been selected
	 */
	public abstract void willAppear(E entity);
}
