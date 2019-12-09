package sec.project.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.User;
import sec.project.repository.UserRepository;

@Controller
public class LoginController {

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

    @RequestMapping("*")
    public String defaultMapping(@CookieValue(value = "token", defaultValue = "") String token) {
        return token.length() > 0 ? "redirect:/home" : "redirect:/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletResponse response) {
        response.addCookie(new Cookie("token", null));
        return "redirect:/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginForm(HttpServletResponse response, Model model, @RequestParam String username, @RequestParam String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            model.addAttribute("error", "User not found");
            return "login";
        }
        if (!user.getPassword().equals(password)) {
            model.addAttribute("error", "Wrong password");
            return "login";
        }
        response.addCookie(new Cookie("token", user.getId().toString()));
        return "redirect:/home";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerForm(HttpServletResponse response, Model model, @RequestParam String username, @RequestParam String fullname, @RequestParam String email, @RequestParam String password) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            model.addAttribute("error", "Username is taken");
            return "login";
        }
        if (username.length() < 1 || fullname.length() < 1 || password.length() < 1 || !email.contains("@")) {
            model.addAttribute("error", "You must provide an username, full name, email and password");
            return "login";
        }
        if (password.length() > 12)
            password = password.substring(0, 12);
        user = userRepository.save(new User(username, password, fullname, email));
        response.addCookie(new Cookie("token", user.getId().toString()));
        return "redirect:/home";
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String userDetails(@CookieValue(value = "token", defaultValue = "") String token, Model model) {
        User user = getAuth(token);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "user";
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public String userUpdateForm(@CookieValue(value = "token", defaultValue = "") String token,
            HttpServletResponse response, Model model,
            @RequestParam String fullname, @RequestParam String email, 
            @RequestParam String password, @RequestParam String address, @RequestParam String cvv,
            @RequestParam String expiry, @RequestParam String cardNumber, @RequestParam String country) {
        User user = getAuth(token);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        if (fullname.length() < 1 || password.length() < 1 || !email.contains("@")) {
            model.addAttribute("error", "You must provide a full name, email and password");
            return "user";
        }
        if (password.length() > 12)
            password = password.substring(0, 12);

        user.setFullname(fullname);
        user.setEmail(email);
        user.setPassword(password);
        user.setAddress(address);
        user.setCardNumber(cardNumber);
        user.setCvv(cvv);
        user.setExpiry(expiry);
        user.setCountry(country);
        
        userRepository.save(user);
        return "redirect:/user";
    }

}
