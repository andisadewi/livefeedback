package DesignsDo;

/*
 * Um sich als Dozent in eine Veranstaltung einzuloggen
 * 
 */

import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import Backend.Lecture;
import Frontend.Broadcaster;
import Frontend.Broadcaster.BroadcastListener;
import Frontend.MainUI;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

@PreserveOnRefresh
public class VeranstaltungsOverViewDo extends Panel implements BroadcastListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6900355369564771211L;
	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */
	@AutoGenerated
	 
	private MainUI main;
 
	/**
	 * The constructor should first build the main layout, set the composition
	 * root and then do any custom initialization.
	 * 
	 * The constructor will not be automatically regenerated by the visual
	 * editor.
	 */ 
	public VeranstaltungsOverViewDo(MainUI main) {
		Broadcaster.register(this);  
		this.main = main;
		setSizeFull();
		setImmediate(true);
		setContent(buildMainVerticalLayout());
	}


	private VerticalLayout buildMainVerticalLayout() {
		VerticalLayout mainVerticalLayout = new VerticalLayout();
		mainVerticalLayout.setWidth("100%");
		mainVerticalLayout.setSpacing(true);
		mainVerticalLayout.setImmediate(true);
		Button neueVeranstaltung = new Button("new",
				new Button.ClickListener() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 3948199698666155602L;

					@Override
					public void buttonClick(ClickEvent event) {
						main.addWindow(buildNewWindow());

					}
				});
		neueVeranstaltung.addStyleName(Reindeer.BUTTON_LINK);
		neueVeranstaltung.setIcon(new ThemeResource("Icons/Faenza/actions/24/list-add.png"));
		mainVerticalLayout.addComponent(neueVeranstaltung);
		Panel[] panels = buildPanels();
		for (int i = 0; i < panels.length; i++) {
			mainVerticalLayout.addComponent(panels[i]);
		}
		return mainVerticalLayout;
	}

	private Panel[] buildPanels() {
		

		Statement stm;
		try {
			stm = Backend.ConnectionManager.Instance.createStatement();

			ArrayList<Lecture> lectureList = deleteOld(Lecture.select(stm));

			Panel[] panels = new Panel[lectureList.size()];

			// Panel [] panels = new Panel[2];

			for (int i = 0; i < panels.length; i++) {
				Lecture lecture = lectureList.get(i);
				panels[i] = new Panel(lecture.getId()+" : "+lecture.getName());
				panels[i].setWidth("650px");
				panels[i].setImmediate(true);
				// Layout des Panels
				HorizontalLayout panelLayout = new HorizontalLayout();
				panelLayout.setSpacing(true);
				panelLayout.setImmediate(true);
				VerticalLayout buttonLayout = buildVerticalLayout(lecture.getId());
				// Informationen zur Veranstaltung
				Panel doName = new Panel("Dozent :");
				doName.setWidth("180px");
				doName.setImmediate(true);
				doName.setContent(new Label(lecture.getProfessorID()));
				Panel raum = new Panel("RaumNr. :");
				raum.setImmediate(true);
				raum.setWidth("180px");
				raum.setContent(new Label(lecture.getRoom()));
				Panel date = new Panel("Datum :");
				date.setImmediate(true);
				date.setWidth("180px");
				date.setContent(new Label(lecture.getDate().toString()));
				
				// zum Panel hinzufuegen
				panelLayout.addComponents(doName);
				panelLayout.addComponent(raum);
				panelLayout.addComponent(date);
				panelLayout.addComponent(buttonLayout);
				buttonLayout.setWidth("50px");
				panels[i].setContent(panelLayout);
			}
			System.out.println("veranstaltungen");
			stm.close();
			return panels;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new Panel[0];
	}

	private VerticalLayout buildVerticalLayout(final int i) {
		VerticalLayout buttonLayout = new VerticalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setImmediate(true);
		// Button zum bearbeiten
		Button bearbeiten = new Button(null,
				new Button.ClickListener() {
					/**
					 * 
					 */
					private static final long serialVersionUID = 867011698484687001L;

					@Override
					public void buttonClick(ClickEvent event) {

						main.addWindow(buildEditLogInWindow(i));
					}
				});
		bearbeiten.addStyleName(Reindeer.BUTTON_LINK);
		bearbeiten.setIcon(new ThemeResource("Icons/Faenza/actions/22/gtk-edit.png"));
		bearbeiten.setWidth("50px");
		bearbeiten.setImmediate(true);
		
		// Button zum einloggen um Vorlesung zu halten
		Button einloggen = new Button(null, new Button.ClickListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -3104156503053369963L;

			@Override
			public void buttonClick(ClickEvent event) {

				main.addWindow(buildLogInWindow(i));

			}
		});
		einloggen.addStyleName(Reindeer.BUTTON_LINK);
		einloggen.setIcon(new ThemeResource("Icons/Faenza/actions/22/forward.png"));
		einloggen.setWidth("50px");
		einloggen.setImmediate(true);

		// Button zum loeschen
		Button loeschen = new Button(null, new Button.ClickListener() {

			private static final long serialVersionUID = 4680334261694304095L;

			@Override
			public void buttonClick(ClickEvent event) {
				main.addWindow(buildDeleteWindow(i));

			}
		});
		loeschen.addStyleName(Reindeer.BUTTON_LINK);
		loeschen.setIcon(new ThemeResource("Icons/Faenza/actions/22/remove.png"));
		loeschen.setWidth("50px");
		loeschen.setImmediate(true);

		buttonLayout.addComponent(bearbeiten);
		buttonLayout.addComponent(loeschen);
		buttonLayout.addComponent(einloggen);
		return buttonLayout;
	}

	private Window buildEditLogInWindow(final int i) {
		
		Statement stm;
		try {
		stm = Backend.ConnectionManager.Instance.createStatement();
		final Lecture lecture = Lecture.selectLecture(stm, i);
		final Window loginWindow = new Window("Login");
		loginWindow.setModal(true);
		loginWindow.setClosable(false);
		loginWindow.setWidth("220px");
		loginWindow.addStyleName(Reindeer.WINDOW_LIGHT);
		VerticalLayout vertLay = new VerticalLayout();
		vertLay.setSpacing(true);

		final PasswordField passw = new PasswordField("password");
		passw.focus();
		Button buttonLogin = new Button("OK!");
		buttonLogin.setClickShortcut(KeyCode.ENTER);
		buttonLogin.addClickListener(new Button.ClickListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1538926467301181747L;

			@Override
			public void buttonClick(ClickEvent event) {
				loginWindow.close();
				String pw = passw.getValue();
				if (pw.equals(lecture.getPassword())) {
					final Window editWindow = buildEditWindow(i);
					main.addWindow(editWindow);

				} else {
					Notification.show("Fehlerhafter Login");
				}
			}
		});

		loginWindow.setContent(vertLay);
		vertLay.addComponent(passw);
		vertLay.setComponentAlignment(passw, Alignment.TOP_CENTER);
		vertLay.addComponent(buttonLogin);
		vertLay.setComponentAlignment(buttonLogin, Alignment.TOP_CENTER);
		loginWindow.center();
		return loginWindow;
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return new Window("Fehler");
		
	}

	private Window buildLogInWindow(int i) {
		Statement stm;
		try {
		stm = Backend.ConnectionManager.Instance.createStatement();
		final Lecture lecture = Lecture.selectLecture(stm, i);
		final Window loginWindow = new Window("Login");
		loginWindow.setModal(true);
		loginWindow.setClosable(false);
		loginWindow.setWidth("220px");
		loginWindow.addStyleName(Reindeer.WINDOW_LIGHT);
		VerticalLayout vertLay = new VerticalLayout();
		vertLay.setSpacing(true);

		final PasswordField passw = new PasswordField("password");
		passw.focus();
		Button buttonLogin = new Button("OK!");
		buttonLogin.setClickShortcut(KeyCode.ENTER);
		buttonLogin.addClickListener(new Button.ClickListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -5240399928433076836L;

			@Override
			public void buttonClick(ClickEvent event) {
				loginWindow.close();
				String pw = passw.getValue();
				main.setVeranstaltung(lecture.getId());
				if (pw.equals(lecture.getPassword())) {
					main.getNavigator().navigateTo(MainUI.ACTIVE);

				} else {
					Notification.show("Fehlerhafter Login");
				}
			}
		});

		loginWindow.setContent(vertLay);
		vertLay.addComponent(passw);
		vertLay.setComponentAlignment(passw, Alignment.TOP_CENTER);
		vertLay.addComponent(buttonLogin);
		vertLay.setComponentAlignment(buttonLogin, Alignment.TOP_CENTER);
		loginWindow.center();
		return loginWindow;
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return new Window("Fehler");
		
	}

	private Window buildEditWindow(int i) {
		final Statement stm;
		try {
		stm = Backend.ConnectionManager.Instance.createStatement();
	    final Lecture lecture = Lecture.selectLecture(stm, i);
		final Window editWindow = new Window("Bearbeiten");
		editWindow.setModal(true);
		editWindow.setClosable(false);
		editWindow.setWidth("650px");
		editWindow.addStyleName(Reindeer.WINDOW_LIGHT);
		VerticalLayout vertLay = new VerticalLayout();
		vertLay.setSpacing(true);

		final TextField name = new TextField("Name");
		name.setValue(lecture.getName());
		name.focus();
		final TextField raum = new TextField("Raum");
		raum.setValue(lecture.getRoom());
		final DateField date = new DateField("Datum");
		Calendar c = new GregorianCalendar();
	    c.set(Calendar.HOUR_OF_DAY, 0); //anything 0 - 23
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);
	    Date today = c.getTime(); 
		date.setRangeStart(today);
		date.setValue(lecture.getDate());
		date.setImmediate(true);
		final TextField passw = new TextField("Passwort");
		passw.setValue(lecture.getPassword());
		final TextField dozent = new TextField("Dozent");
		dozent.setValue(lecture.getProfessorID());
		final TextField speedLimit = new TextField("SpeedLimit");
		speedLimit.setValue(""+lecture.getSpeedLimit());
		final TextField volumeLimit = new TextField("VolumeLimit");
		volumeLimit.setValue(""+lecture.getVolumeLimit());
		final TextField votingLimit= new TextField("VotingLimit");
		votingLimit.setValue(""+lecture.getVotingLimit());
		

		Button buttonOK = new Button("OK!");
		buttonOK.setClickShortcut(KeyCode.ENTER);
		buttonOK.addClickListener(new Button.ClickListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 9005574278651842676L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				if (!correctINT(speedLimit.getValue()) && !correctINT(volumeLimit.getValue()) && !correctINT(votingLimit.getValue()) && date.getValue()!=null){
					editWindow.close();
					lecture.setDate(date.getValue());
					lecture.setProfessorID(dozent.getValue());
					lecture.setRoom(raum.getValue());
					lecture.setName(name.getValue());
					lecture.setPassword(passw.getValue());
					lecture.setSpeedLimit(Integer.parseInt(speedLimit.getValue()));
					lecture.setVolumeLimit(Integer.parseInt(volumeLimit.getValue()));
					lecture.setVotingLimit(Integer.parseInt(votingLimit.getValue()));
			
					try {
						lecture.update(stm);
						if (isToday(lecture.getDate())) Broadcaster.broadcast("LectureToday");
						else Broadcaster.broadcast("Lecture");
						Notification.show("Erfolgreich!");
					} catch (SQLException e) {
						Notification.show("Fehler beim bearbeiten!");
						e.printStackTrace();
					}
					
				}
				else {
					Notification.show("SpeedLimit & VolumeLimit & VotingLimit muessen Zahlen sein!Und das Datum in der Zukunft liegen!");
				}
			}
		});
		
		Button abbruch = new Button("Abbruch!", new Button.ClickListener() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 7218815404815849526L;

			@Override
			public void buttonClick(ClickEvent event) {
				editWindow.close();
			}
		});
		editWindow.setContent(vertLay);
		
		//Felder
		HorizontalLayout horizontal = new HorizontalLayout();
		VerticalLayout vertleft = new VerticalLayout();
		vertleft.setWidth("300px");
		vertleft.setSpacing(true);
		VerticalLayout vertright = new VerticalLayout();
		vertright.setWidth("300px");
		vertright.setSpacing(true);
		vertLay.addComponent(horizontal);
		horizontal.addComponent(vertleft);
		horizontal.addComponent(vertright);
		vertleft.addComponent(name);
		vertleft.addComponent(dozent);
		vertleft.addComponent(votingLimit);
		vertleft.addComponent(volumeLimit);
		vertright.addComponent(raum);
		vertright.addComponent(passw);
		vertright.addComponent(speedLimit);
		
		vertleft.setComponentAlignment(dozent, Alignment.TOP_CENTER);
		vertleft.setComponentAlignment(name, Alignment.TOP_CENTER);
		vertleft.setComponentAlignment(votingLimit, Alignment.TOP_CENTER);
		vertleft.setComponentAlignment(volumeLimit, Alignment.TOP_CENTER);
		vertright.setComponentAlignment(raum, Alignment.TOP_CENTER);
		vertright.setComponentAlignment(passw, Alignment.TOP_CENTER);
		vertright.setComponentAlignment(speedLimit, Alignment.TOP_CENTER);
		//Date + Buttons 
		vertLay.addComponent(date);
		HorizontalLayout buttonLayout= new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setSizeFull();
		buttonLayout.addComponent(abbruch);
		buttonLayout.addComponent(buttonOK);
		buttonLayout.setComponentAlignment(abbruch, Alignment.BOTTOM_CENTER);
		buttonLayout.setComponentAlignment(buttonOK, Alignment.BOTTOM_CENTER);
		vertLay.addComponent(buttonLayout);
		vertLay.setSpacing(true);

		vertLay.setComponentAlignment(date, Alignment.TOP_CENTER);
		vertLay.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
		
		editWindow.center();
		return editWindow;
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return new Window("Fehler");
	
	}

	private Window buildNewWindow() {
		final Statement stm;
		try {
		stm = Backend.ConnectionManager.Instance.createStatement();
		final Window newWindow = new Window("Erstellen");
		newWindow.setModal(true);
		newWindow.setClosable(false);
		newWindow.setWidth("650px");
		newWindow.addStyleName(Reindeer.WINDOW_LIGHT);
		VerticalLayout vertLay = new VerticalLayout();

		final TextField name = new TextField("Name");
		name.setValue("Name");
		name.focus();
		final TextField raum = new TextField("Raum");
		raum.setValue("Raum");
		final DateField date = new DateField("Datum");
		Calendar c = new GregorianCalendar();
	    c.set(Calendar.HOUR_OF_DAY, 0); //anything 0 - 23
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);
	    Date today = c.getTime(); 
		date.setRangeStart(today);
		date.setValue(null);
		final TextField passw = new TextField("Passwort");
		passw.setValue("Passwort");
		final TextField dozent = new TextField("Dozent");
		dozent.setValue("Dozent");
		final TextField speedLimit = new TextField("SpeedLimit");
		speedLimit.setValue("100");
		final TextField volumeLimit = new TextField("VolumeLimit");
		volumeLimit.setValue("100");
		final TextField votingLimit= new TextField("VotingLimit");
		votingLimit.setValue("100");
		
		
		Button buttonOK = new Button("Erstellen");
		buttonOK.setClickShortcut(KeyCode.ENTER);
		buttonOK.addClickListener(new Button.ClickListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -5627807924468836108L;

			@Override
			public void buttonClick(ClickEvent event) {
				Lecture lecture;
				if (!correctINT(speedLimit.getValue()) && !correctINT(volumeLimit.getValue()) && !correctINT(votingLimit.getValue()) && date.getValue()!=null){
					newWindow.close();
					lecture = new Lecture(date.getValue(),dozent.getValue(),raum.getValue(),name.getValue(),
						passw.getValue(),0,0,Integer.parseInt(speedLimit.getValue()),Integer.parseInt(volumeLimit.getValue()),
						Integer.parseInt(votingLimit.getValue()));
						Notification.show("Erfolgreich!");
						if (isToday(date.getValue())) Broadcaster.broadcast("LectureToday");
						else Broadcaster.broadcast("Lecture");
					try {
						lecture.insert(stm);
					} catch (SQLException | ParseException e) {
						e.printStackTrace();
						Notification.show("Fehler beim Erstellen!");
					}
				}
				else {
					Notification.show("SpeedLimit & VolumeLimit & VotingLimit muessen Zahlen sein!Und das Datum in der Zukunft liegen!");
				}
			}
		});

		Button abbruch = new Button("Abbruch!", new Button.ClickListener() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -7011342081793306723L;

			@Override
			public void buttonClick(ClickEvent event) {
				newWindow.close();
			}
		});
		HorizontalLayout horizontal = new HorizontalLayout();
		VerticalLayout vertleft = new VerticalLayout();
		vertleft.setWidth("300px");
		vertleft.setSpacing(true);
		VerticalLayout vertright = new VerticalLayout();
		vertright.setWidth("300px");
		vertright.setSpacing(true);
		vertLay.addComponent(horizontal);
		horizontal.addComponent(vertleft);
		horizontal.addComponent(vertright);
		vertleft.addComponent(name);
		vertleft.addComponent(dozent);
		vertleft.addComponent(votingLimit);
		vertleft.addComponent(volumeLimit);
		vertright.addComponent(raum);
		vertright.addComponent(passw);
		vertright.addComponent(speedLimit);
		
		vertleft.setComponentAlignment(dozent, Alignment.TOP_CENTER);
		vertleft.setComponentAlignment(name, Alignment.TOP_CENTER);
		vertleft.setComponentAlignment(votingLimit, Alignment.TOP_CENTER);
		vertleft.setComponentAlignment(volumeLimit, Alignment.TOP_CENTER);
		vertright.setComponentAlignment(raum, Alignment.TOP_CENTER);
		vertright.setComponentAlignment(passw, Alignment.TOP_CENTER);
		vertright.setComponentAlignment(speedLimit, Alignment.TOP_CENTER);
		
		newWindow.setContent(vertLay);

		vertLay.addComponent(date);
		HorizontalLayout buttonLayout= new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setSizeFull();
		buttonLayout.addComponent(abbruch);
		buttonLayout.addComponent(buttonOK);
		buttonLayout.setComponentAlignment(abbruch, Alignment.BOTTOM_CENTER);
		buttonLayout.setComponentAlignment(buttonOK, Alignment.BOTTOM_CENTER);
		vertLay.addComponent(buttonLayout);
		vertLay.setSpacing(true);

		vertLay.setComponentAlignment(date, Alignment.TOP_CENTER);
		vertLay.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
		newWindow.center();
		return newWindow;
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return new Window("Fehler");
	
	}

	private Window buildDeleteWindow(int i) {
		final Statement stm;
		try {
		stm = Backend.ConnectionManager.Instance.createStatement();
		final Lecture lecture = Lecture.selectLecture(stm, i);
		final Window deleteWindow = new Window("Loeschen");
		deleteWindow.setModal(true);
		deleteWindow.setClosable(false);
		deleteWindow.setWidth("220px");
		deleteWindow.addStyleName(Reindeer.WINDOW_LIGHT);
		VerticalLayout vertLay = new VerticalLayout();
		vertLay.setSpacing(true);

		final PasswordField passw = new PasswordField("password");
		passw.focus();
		Button loeschenButton = new Button("Loeschen");
		loeschenButton.setClickShortcut(KeyCode.ENTER);
		loeschenButton.addClickListener(new Button.ClickListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8069879981578720074L;

			@Override
			public void buttonClick(ClickEvent event) {
				deleteWindow.close();
				String pw = passw.getValue();
				if (pw.equals(lecture.getPassword())) {
					try {
						lecture.delete(stm);
						Notification.show("Erfolgreich!");
						if (isToday(lecture.getDate())) Broadcaster.broadcast("LectureToday");
						else Broadcaster.broadcast("Lecture");
					} catch (SQLException e) {
						e.printStackTrace();
						Notification.show("Fehler beim Loeschen!");
					}

				} else {
					Notification.show("Fehlerhafter Login");
				}
			}
		});

		deleteWindow.setContent(vertLay);
		vertLay.addComponent(passw);
		vertLay.setComponentAlignment(passw, Alignment.TOP_CENTER);
		vertLay.addComponent(loeschenButton);
		vertLay.setComponentAlignment(loeschenButton, Alignment.TOP_CENTER);
		deleteWindow.center();
		return deleteWindow;
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return new Window("Fehler");
	
	}
	private boolean correctINT(String i){
		try { 
			@SuppressWarnings("unused")
			int buffer = Integer.parseInt(i);
		} catch (Exception e) { 
			return true;
		}
		return false;
	}

	public void receiveBroadcast(final String message) {
		try{
			main.access(new Runnable() {				
				@Override
				public void run() {
					
					if( message.equals("LectureToday") || message.equals("Lecture")){
									setContent(buildMainVerticalLayout());
									
									
					}
				}});
			}catch(Exception e){ e.printStackTrace();}
	}
	
	public void detach(){
		Broadcaster.unregister(this);
		super.detach();
	}
	
	private boolean isToday(Date date){
		 Calendar cal1 = Calendar.getInstance();
	        cal1.setTime(date);
	    Calendar cal2 = Calendar.getInstance();
		return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
               cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
		
		
	}
	private ArrayList<Lecture> deleteOld(ArrayList<Lecture> lectures){
		ArrayList<Lecture> lecs = new ArrayList<Lecture>();
		for (int i=0 ; i< lectures.size();i++){
			if (isOld(lectures.get(i).getDate())){
				System.out.print("loeschen");
				try {
					Statement stm = Backend.ConnectionManager.Instance.createStatement();
					lectures.get(i).delete(stm);
				}catch (Exception e){
					e.printStackTrace();
				}
			}else {
				lecs.add(lectures.get(i));
			}	
		}
		return lecs;
	}
	
	private boolean isOld(Date date){
		 Calendar cal1 = Calendar.getInstance();
		 cal1.setTime(date);
		 Calendar cal2 = Calendar.getInstance();
		 if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA)) return true;
		 if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA)) return false;
		 if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)) return true;
		 if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR)) return false;
		 return cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR);
		
	}


}