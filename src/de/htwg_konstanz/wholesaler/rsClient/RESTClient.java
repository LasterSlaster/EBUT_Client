package de.htwg_konstanz.wholesaler.rsClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.print.attribute.standard.Media;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RESTClient {

	public static int showMenu() {
		Scanner s = new Scanner(System.in);
		int choice;
		while (true) {
			System.out.println("\n\n1 - Import productcatalog");
			System.out.println("2 - Export all articles");
			System.out.println("3 - Export selective articles");
			System.out.print("> choice: ");
			choice = s.nextInt();
			if (choice >= 1 && choice <= 3) {
				return choice;
			}
		}
	}

	public static int getExportFormat() {
		Scanner s = new Scanner(System.in);
		int choice;
		while (true) {
			System.out.println("1 - Export XHTML");
			System.out.println("2 - Export XML");
			System.out.print("> choice: ");
			choice = s.nextInt();
			if (choice >= 1 && choice <= 2) {
				return choice;
			}
		}
	}
	
	public static String getSearchText() {
		String searchtext = "";
		Scanner s = new Scanner(System.in);
		while(true) {
			System.out.print("Please enter your searchtext: > ");
			searchtext = s.next();
			if(searchtext.trim() != "") {
				return searchtext;
			}
		}
	}
	
	public static File getFile() {
		String fileName = "";
		Scanner s = new Scanner(System.in);
		while(true) {
			System.out.print("Please enter your filename: > ");
			fileName = s.next();
			if(fileName.trim() != "") {
				File file=new File(fileName);
				if(file.exists() && file.isFile()) {
					return file;
				} else {
					System.out.println(fileName+" not found!");
				}
			}
		}
	}
	
	public static void createXML(String content) throws Exception {
		File articles = new File("articles.xml");
		articles.createNewFile();
		FileWriter writer = new FileWriter(articles);
		writer.write("");
		writer.write(content);
		writer.flush();
		writer.close();
	}
	
	public static void createXHTML(String content) throws Exception {
		File articles = new File("articles.html");
		articles.createNewFile();
		FileWriter writer = new FileWriter(articles);
		writer.write("");
		writer.write(content);
		writer.flush();
		writer.close();
	}

	public static void main(String[] args) throws IOException {
		
		Client client = ClientBuilder.newClient();
		String baseUrl = "http://localhost:8080/ebut/rest/catalog/";
		Response res;
		String response;

		while (true) {
			int option = RESTClient.showMenu();
			int formatOption;
			switch (option) {
			case 1:
				File file = RESTClient.getFile();
				//convert to inputstream for sending
				InputStream inputStream = new FileInputStream(file);
				res = client.target(baseUrl).request(MediaType.APPLICATION_XML).post(Entity.entity(inputStream, MediaType.APPLICATION_XML));
				response = res.readEntity(String.class);
				if(res.getStatus() != 200) {
					System.out.println("ERROR!");
					System.out.println(response);
				} else {
					System.out.println(response);
					System.out.println("catalog successfully imported!");
				}
				break;
			case 2:
				formatOption = RESTClient.getExportFormat();
				//export all HTML
				if (formatOption == 1) {
					res = client.target(baseUrl+"xhtml").request(MediaType.APPLICATION_XHTML_XML).get();
					response = res.readEntity(String.class);
					if(res.getStatus() != 200) {
						System.out.println("FEHLER!");
						System.out.println(response);
					} else {
						try {
							RESTClient.createXHTML(response);
							System.out.println("successfully exportet all articles as XHTML!");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				//export all XML	
				} else if (formatOption == 2) {
					res = client.target(baseUrl+"xml").request(MediaType.APPLICATION_XML).get();
					response = res.readEntity(String.class);
					if(res.getStatus() != 200) {
						System.out.println("ERROR!");
						System.out.println(response);
					} else {
						try {
							RESTClient.createXML(response);
							System.out.println("successfully exportet all articles as XML!");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				break;
			case 3:
				String searchText = RESTClient.getSearchText();
				formatOption = RESTClient.getExportFormat();
				//export selective HTML
				if (formatOption == 1) {
					res = client.target(baseUrl+"xhtml?search="+searchText).request(MediaType.APPLICATION_XHTML_XML).get();
					response = res.readEntity(String.class);
					if(res.getStatus() != 200) {
						System.out.println("FEHLER!");
						System.out.println(response);
					} else {
						try {
							RESTClient.createXHTML(response);
							System.out.println("successfully exportet articles that match on "+searchText+" as XHTML!");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				//export selective XML	
				} else if (formatOption == 2) {
					res = client.target(baseUrl+"xml?search="+searchText).request(MediaType.APPLICATION_XML).get();
					response = res.readEntity(String.class);
					if(res.getStatus() != 200) {
						System.out.println("FEHLER!");
						System.out.println(response);
					} else {
						try {
							RESTClient.createXML(response);
							System.out.println("successfully exportet articles that match on "+searchText+" as XML!");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				break;
			default:
				break;
			}
		}

		// Client client = ClientBuilder.newClient();

		// Trivial get
		// String request = "Give me this text back";
		// Response res =
		// client.target("http://localhost:8080/WholesalerWebDemo/rest/hello/"+request)
		// .request(MediaType.TEXT_PLAIN)
		// .get();

		// String response = res.readEntity(String.class);
		// System.out.println(response);

		// Easy get
		// int num = 2;
		// Response res2 =
		// client.target("http://localhost:8080/WholesalerWebDemo/rest/hello/square/"+num)
		// .request(MediaType.TEXT_PLAIN)
		// .get();

		// String response2 = res2.readEntity(String.class);
		// System.out.println(response2);

		// Easy post
		// Response res3 =
		// client.target("http://localhost:8080/WholesalerWebDemo/rest/hello/squareroot")
		// .request(MediaType.TEXT_PLAIN)
		// .post(Entity.entity(25, MediaType.TEXT_PLAIN));
		// String response3 = res3.readEntity(String.class);
		// System.out.println(response3);
	}

}
