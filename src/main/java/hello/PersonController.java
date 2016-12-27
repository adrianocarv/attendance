package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PersonController {

  @Autowired
  private PersonRepository personRepository;

  /**
   * GET /create  --> Create a new user and save it in the database.
   */
  @RequestMapping("/create")
  @ResponseBody
  public String create(String name, Long entity_id) {
    String userId = "";
    try {
      Person person = new Person(name, entity_id);
      personRepository.save(person);
      userId = String.valueOf(person.getId());
    }
    catch (Exception ex) {
      return "Error creating the user: " + ex.toString();
    }
    return "User succesfully created with id = " + userId;
  }
  
  /**
   * GET /delete  --> Delete the user having the passed id.
   */
  @RequestMapping("/delete")
  @ResponseBody
  public String delete(long id) {
    try {
        Person person = new Person(id);
        personRepository.delete(person);
    }
    catch (Exception ex) {
      return "Error deleting the user:" + ex.toString();
    }
    return "User succesfully deleted!";
  }
  
  /**
   * GET /get-by-email  --> Return the id for the user having the passed
   * email.
   */
  @RequestMapping("/get-by-name")
  @ResponseBody
  public String getByName(String name) {
    String personId = "";
    try {
      Person person = personRepository.findByName(name);
      personId = String.valueOf(person.getId());
    }
    catch (Exception ex) {
      return "User not found";
    }
    return "The person id is: " + personId;
  }
  
  /**
   * GET /update  --> Update the email and the name for the user in the 
   * database having the passed id.
   */
  @RequestMapping("/update")
  @ResponseBody
  public String updateUser(long id, String email, String name) {
    try {
      Person person = personRepository.findOne(id);
      person.setMail(email);
      person.setName(name);
      personRepository.save(person);
    }
    catch (Exception ex) {
      return "Error updating the user: " + ex.toString();
    }
    return "User succesfully updated!";
  }

}