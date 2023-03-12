package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {

	private Scanner scanner = new Scanner(System.in);
	//Scanner to read from the user input
	private ProjectService projectService = new ProjectService();
	private Project curProject;
	
	private List<String> operations = List.of("" + 
	"1) Add a project",
	"2) List projects",
	"3) Select a project");
	//display list for menu options
	public static void main(String[] args) {

		new ProjectsApp().processUserSelections();
		//main method will run the processUserSelection or the menu selection
		
	}
	
	private void processUserSelections() {
		boolean done = false;

		while (!done) {
			try {
				int selection = getUserSelection();
				switch (selection) {
				case -1:
					done = exitMenu();
					break;
				case 1:
					createProject();
					break;
				default:
					System.err.println("\n" + selection + " is not a valid selection. Try again.");
				case 2:
					listProjects();
					break;
				case 3:
					selectProject();
					break;
				}

			} catch (Exception e) {
				System.out.println("\nError: " + e + " Try again. ");
			}
		}
	}//this method will display menu selection, get the selection from user and
	private void selectProject() {
		
	listProjects();
	//Pg 6 4a - this will call listProjects to print a List of Projects
	Integer projectId = getIntInput("Enter a project ID to select a project");
	//4b
	curProject = null;
	//4c
	curProject = projectService.fetchProjectById(projectId);
	}

	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects:");

		projects.forEach(project -> System.out.
			println("   " + project.getProjectId()
			+ ": " + project.getProjectName()));
	}

	//do the selection

	private void createProject() {
		String projectName = getStringInput("Enter the project name: ");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours: ");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours: ");
		Integer difficulty = getIntInput("Enter the project difficulty(1-5)");
		if( difficulty >= 5) {
			difficulty = 5;
		} else if (difficulty <= 0) {
			difficulty = 1;
		}//this make sure that the difficulty defaults to 5 as the highest 
		//and 1 as the lowest even when input is more than 5 or lower than 1
		String notes = getStringInput("Enter the project notes: ");
		
		Project project = new Project();
		
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
	}//this is to gather project details/information

	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number.");
		}//this method prompts the user for a decimal number input
		//takes the input and convert it as a BigDecimal with a precision of
		//two decimal places
	}

	private boolean exitMenu() {
		System.out.println("\nExiting menu");
		return true;
	}//this just returns true and exit menu

	private int getUserSelection() {
		printOperations();

		Integer input = getIntInput("Enter a menu selection");
		return Objects.isNull(input) ? -1 : input;
	}//this will get the user input take it as Integer

	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return Integer.valueOf(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}//this takes the user input 

	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the Enter key to quit: ");
		operations.forEach(input -> System.out.println("   " + input));
		
		if(Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		}
		else {
			System.out.println("\nYou are working with project: " + curProject);
		}
	}//this method just prints the available selections 
	

	private String getStringInput(String prompt) {

		System.out.print(prompt + ": ");
		String input = scanner.nextLine();
		return input.isBlank() ? null : input.trim();
	}//takes user input make sure it's not null and removes any whitespace

}
