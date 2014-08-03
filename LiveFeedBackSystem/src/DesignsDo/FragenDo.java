package DesignsDo;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Backend.Question;
import Backend.Voting;
import Frontend.Broadcaster;
import Frontend.Broadcaster.BroadcastListener;
import Frontend.MainUI;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
public class FragenDo extends Panel implements BroadcastListener  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1586709343478126255L;
	private MainUI main;
	

	
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */ 
	public FragenDo(MainUI main) { 
		Broadcaster.register(this);
		this.main=main;
		setSizeFull();
		setCaption("Fragen :");
		setContent(buildMainVertical());
			
		 
	} 
	@SuppressWarnings("unused")
	private VerticalLayout buildMainVertical (){
		if (main.getVeranstaltung()==0){
			VerticalLayout mainVertical = new VerticalLayout();
			mainVertical.setImmediate(false);
			mainVertical.setSizeFull();
			mainVertical.setMargin(false);
			mainVertical.setSpacing(true);
			mainVertical.addComponent(new Label("Noch keine Veranstaltung"));
			return mainVertical;
		}else {
			
			VerticalLayout mainVertical=new VerticalLayout();
			mainVertical.setWidth("100%");
			mainVertical.setSpacing(true);
			final Statement stm;
			ArrayList<Question> questionList = new ArrayList<Question>();
			try {
				
				stm = Backend.ConnectionManager.Instance.createStatement();
				ArrayList<Question> questionsUnsort = Question.selectAll(stm,main.getVeranstaltung());
				if((questionsUnsort.size())>0){
				int[] votes = new int[questionsUnsort.size()];
	            for (int i = 0; i < votes.length; i++) {
					ArrayList<Voting> votings = Voting.selectFromID(stm, questionsUnsort.get(i).getId());
					votes[i]=votings.size();
					}
	            int index=0;
	            for (int i = 0; i < questionsUnsort.size(); i++) {
	            	 int bufferVotings=0;
	            	 boolean run=false;
					for (int j = 0; j < votes.length; j++) {
						if(votes[j]==-1){
							continue;
						}
						if (votes[j]>bufferVotings) {
							bufferVotings=votes[j];
							index=j;
							run=true;
						}
					}
					if(run){
					questionList.add(questionsUnsort.get(index));
					votes[index]=-1;
					index=0;
					}
				}
	            for (int i = 0; i < votes.length; i++) {
					if(votes[i]!=-1){
						questionList.add(questionsUnsort.get(i));
					}
				}
	            }else{questionList=questionsUnsort;}	
		
				if (questionList==null) return mainVertical;
				
				Panel[] panels = new Panel[questionList.size()];
				for (int i =0;i<panels.length;i++){
					panels[i]=buildPanel(i,questionList.get(i));
					mainVertical.addComponent(panels[i]);
				}
				return mainVertical;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	private Panel buildPanel(int i,final Question question){
		final Statement stm;
		try {
		
			stm = Backend.ConnectionManager.Instance.createStatement();
			
		Panel buffer= new Panel("Frage "+(i+1)+":");
		buffer.setSizeFull();
		HorizontalLayout panelHorizontal = new HorizontalLayout();
		buffer.setContent(panelHorizontal);
		panelHorizontal.setSizeFull();
		panelHorizontal.setSpacing(true);
		
		
		ArrayList<Voting> votes=Voting.selectFromID(stm, question.getId());
		
		
		//Datenabfragen
				Panel questionLabel = new Panel("Frage :");
				questionLabel.setWidth("100%");
				questionLabel.setHeight("100%");
		//		questionLabel.setContent(new Label("Das hier ist die super coole Frage.Da das erst 35 Zeichen waren, wir aber 160 brauchen, steht hier mehr! Und nun haben wir 125, deswegen schreibe ich noch mehr!"));
				questionLabel.setContent(new Label(question.getText()));
				panelHorizontal.addComponent(questionLabel);
				if (question.getAnswered()){
					Image answered = new Image();
					answered.setSource(new ThemeResource("Icons/Faenza/actions/24/dialog-ok.png"));
					panelHorizontal.addComponent(answered);
				}else{
				
				
		//zaehler
				Panel vote = new Panel("Vote");
				vote.setWidth("50px");
				vote.setHeight("100%");
				vote.setContent(new Label(""+votes.size()));
				panelHorizontal.addComponent(vote);
				
		//AnsweredButton	
				Button answered = new Button();
				answered.addClickListener(new Button.ClickListener() {
					
					/**
					 * 
					 */
					private static final long serialVersionUID = -7025912657625335026L;

					@Override
					public void buttonClick(ClickEvent event) {
						final Statement stm;
						try {
							
							stm = Backend.ConnectionManager.Instance.createStatement();
							question.updateAnswered(stm, true);
							Broadcaster.broadcast("answered");
							Notification.show("Erfolg");
						} catch (SQLException e) {
							Notification.show("Fehler");
							e.printStackTrace();
						}
					}
				});
				answered.addStyleName(Reindeer.BUTTON_LINK);
				answered.setIcon(new ThemeResource("Icons/Faenza/actions/22/dialog-ok.png"));
				answered.setWidth("50px");
		//DeleteButton 		
				Button delete = new Button();
				delete.addClickListener(new Button.ClickListener() {
						
					/**
					 * 
					 */
					private static final long serialVersionUID = 4614241709189485481L;

					@Override
					public void buttonClick(ClickEvent event) {
						final Statement stm;
						try {
							stm = Backend.ConnectionManager.Instance.createStatement();
							
							question.delete(stm);
							Notification.show("Erfolg");
							Broadcaster.broadcast("deleteQuestion");
						} catch (SQLException e) {
							Notification.show("Fehler");
							e.printStackTrace();
						}
					}	
				});
				delete.addStyleName(Reindeer.BUTTON_LINK);
				delete.setIcon(new ThemeResource("Icons/Faenza/actions/22/remove.png"));
				delete.setWidth("50px");
				panelHorizontal.addComponent(answered);
				panelHorizontal.addComponent(delete);
				panelHorizontal.setExpandRatio(vote, 1f);
				panelHorizontal.setExpandRatio(delete, 1f);
				panelHorizontal.setExpandRatio(answered, 1f);
				}
				panelHorizontal.setExpandRatio(questionLabel, 5.5f);
				
				
				return buffer;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new Panel();
		
	}
	
	
	
	
	public void receiveBroadcast(final String message) {
		try{
			main.access(new Runnable() {				
				@Override
				public void run() {
				
					//TODO cases and push
					if(message.equals("updateQuestion") || message.equals("up") || message.equals("answered") || message.equals("deleteQuestion")){
									setContent(buildMainVertical());
									
									
					}
				}});
			}catch(Exception e){ e.printStackTrace();}
	}
	
	public void detach(){
		Broadcaster.unregister(this);
		super.detach();
	}


}
