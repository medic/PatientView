package net.frontlinesms.plugins.patientview.ui;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;

public abstract class ViewHandler implements ThinletUiEventHandler{

	protected UiGeneratorController ui;
	protected ApplicationContext appCon;
	
	protected Object mainPanel;

	protected List<ViewHandler> subviews;
	
	public ViewHandler(UiGeneratorController ui, ApplicationContext appCon){
		this.ui = ui;
		this.appCon = appCon;
		mainPanel = Thinlet.create("panel");
		ui.setWeight(mainPanel, 1, 1);
		subviews= new ArrayList<ViewHandler>();
	}
	
	public ViewHandler(UiGeneratorController ui, ApplicationContext appCon, String xmlPath){
		this.ui = ui;
		this.appCon = appCon;
		if(xmlPath != null && !xmlPath.trim().equals("")){ 
			mainPanel = ui.loadComponentFromFile(xmlPath, this);
		}
		subviews= new ArrayList<ViewHandler>();
	}
	
	public Object getMainPanel() {
		return mainPanel;
	}
	
	protected void setMainPanel(Object mainPanel){
		if(this.mainPanel !=null){
			Object[] children = ui.getItems(this.mainPanel);
			for(Object o: children){
				ui.add(mainPanel,o);
			}
		}
		this.mainPanel = mainPanel;
	}
	
	public void add(Object thinletObject){
		ui.add(mainPanel,thinletObject);
	}
	
	public void add(Object thinletContainer, Object toAdd){
		ui.add(thinletContainer, toAdd);
	}
	
	public void addSubview(ViewHandler view){
		view.willAppear();
		subviews.add(view);
		add(view.getMainPanel());
	}
	
	public void addSubview(Object thinletContainer, ViewHandler view){
		view.willAppear();
		subviews.add(view);
		add(thinletContainer,view.getMainPanel());
	}
	
	public void remove(Object thinletObject){
		ui.remove(thinletObject);
	}
	
	public boolean removeSubview(ViewHandler view){
		view.willDisappear();
		ui.remove(view.getMainPanel());
		return subviews.remove(view);
	}
	
	protected void removeAll(){
		ui.removeAll(mainPanel);
	}
	
	protected void removeAll(Object toRemoveFrom){
		ui.removeAll(toRemoveFrom);
	}
	
	protected Object find(String objectName){
		return ui.find(mainPanel, objectName);
	}
	
	protected Object find(Object container, String objectName){
		return ui.find(container, objectName);
	}
	
	public void willAppear(){
		subviewsWillAppear();
	}
	
	public void willDisappear(){
		subviewsWillDisappear();
	}
	
	protected void subviewsWillAppear(){
		for(ViewHandler view: subviews){
			view.willAppear();
		}
	}
	
	protected void subviewsWillDisappear(){
		for(ViewHandler view: subviews){
			view.willDisappear();
		}
	}
	
	protected String getI18nString(String key){
		return InternationalisationUtils.getI18nString(key);
	}
}