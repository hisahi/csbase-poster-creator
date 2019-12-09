
package sec.project.controller; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sec.project.domain.User;
import sec.project.repository.UserRepository;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;
    
    private User getAuth(String token) {
        try {
            long tkid = Long.parseLong(token);
            return userRepository.findById(tkid);
        } catch (Exception ex) {
            return null;
        }
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String homepage(@CookieValue(value = "token", defaultValue = "") String token, Model model) {
        User user = getAuth(token);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "home";
    }
    
}
