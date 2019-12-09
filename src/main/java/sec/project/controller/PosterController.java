
package sec.project.controller; 

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import sec.project.domain.Order;
import sec.project.domain.Poster;
import sec.project.domain.User;
import sec.project.repository.OrderRepository;
import sec.project.repository.PosterRepository;
import sec.project.repository.UserRepository;

@Controller
public class PosterController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PosterRepository posterRepository;
    @Autowired
    private OrderRepository orderRepository;
    
    private User getAuth(String token) {
        try {
            long tkid = Long.parseLong(token);
            return userRepository.findById(tkid);
        } catch (Exception ex) {
            return null;
        }
    }

    @RequestMapping(value = "/you", method = RequestMethod.GET)
    public String yourPosters(@CookieValue(value = "token", defaultValue = "") String token, Model model) {
        User user = getAuth(token);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        model.addAttribute("posters", posterRepository.findByUserId(user));
        return "you";
    }

    @RequestMapping(value = "/browse", method = RequestMethod.GET)
    public String browsePosters(@CookieValue(value = "token", defaultValue = "") String token, Model model) {
        User user = getAuth(token);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        model.addAttribute("posters", posterRepository.findByIsPublicTrue());
        return "you";
    }

    @RequestMapping(value = "/poster/{posterId}", method = RequestMethod.GET)
    public String posterInfo(@CookieValue(value = "token", defaultValue = "") String token, Model model, @PathVariable(value="posterId") String id) {
        User user = getAuth(token);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        Poster poster;
        try {
            poster = posterRepository.findById(Long.parseLong(id));
            poster.getId();
        } catch (Exception ex) {
            return "redirect:/home";
        }
        model.addAttribute("poster", poster);
        return "poster";
    }

    @RequestMapping(value = "/poster/{posterId}/image", method = RequestMethod.GET)
    public @ResponseBody byte[] posterImage(@CookieValue(value = "token", defaultValue = "") String token, @PathVariable(value="posterId") String id) {
        User user = getAuth(token);
        if (user == null) return new byte[] {};
        try {
            return posterRepository.findById(Long.parseLong(id)).getImage();
        } catch (Exception ex) {
            return new byte[] {};
        }
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newPoster(@CookieValue(value = "token", defaultValue = "") String token, Model model) {
        User user = getAuth(token);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "new";
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public String newPosterForm(@CookieValue(value = "token", defaultValue = "") String token,
            @Valid @RequestPart("image") MultipartFile multipart,
            HttpServletResponse response, Model model,
            @RequestParam String title, @RequestParam String description, @RequestParam(required = false) String isPublic) {
        User user = getAuth(token);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        if (title.length() < 1 || title.length() > 100 || description.length() > 5000) {
            model.addAttribute("error", "Invalid poster title or description");
            return "new";
        }
        
        String fn = multipart.getOriginalFilename().toLowerCase();
        
        if (!(fn.endsWith(".png") || fn.endsWith(".jpg") || fn.endsWith(".jpeg") || fn.endsWith(".gif") || fn.endsWith(".bmp"))) {
            model.addAttribute("error", "File was not an image!");
            return "new";
        }
        
        byte[] image;
        try {
            image = multipart.getBytes();
        } catch (IOException ex) {
            model.addAttribute("error", "Cannot open poster image");
            return "new";
        }
        Poster poster = new Poster(image, title, description, user, isPublic != null);
        
        posterRepository.save(poster);
        return "redirect:/poster/" + poster.getId();
    }
    
    @RequestMapping(value = "/poster/{posterId}/order", method = RequestMethod.GET)
    public String orderPoster(@CookieValue(value = "token", defaultValue = "") String token, Model model, @PathVariable(value="posterId") String id) {
        User user = getAuth(token);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        Poster poster;
        try {
            poster = posterRepository.findById(Long.parseLong(id));
            poster.getId();
        } catch (Exception ex) {
            return "redirect:/home";
        }
        model.addAttribute("poster", poster);
        return "order";
    }

    @RequestMapping(value = "/poster/{posterId}/order", method = RequestMethod.POST)
    public String orderPosterForm(@CookieValue(value = "token", defaultValue = "") String token,
            @PathVariable(value="posterId") String id,
            HttpServletResponse response, Model model,
            @RequestParam String address, @RequestParam String size, @RequestParam int qty) {
        User user = getAuth(token);
        if (user == null) return "redirect:/login";
        Poster poster;
        try {
            poster = posterRepository.findById(Long.parseLong(id));
            poster.getId();
        } catch (Exception ex) {
            return "redirect:/home";
        }
        model.addAttribute("user", user);
        if (address.length() < 1) {
            model.addAttribute("poster", poster);
            model.addAttribute("error", "Must provide address to ship to");
            return "order";
        }
        
        int price = (size.equals("normal") ? 20 : 0) * qty;
        
        model.addAttribute("poster", poster);
        model.addAttribute("address", address);
        model.addAttribute("size", size);
        model.addAttribute("qty", qty);
        model.addAttribute("price", price);
        
        Order order = new Order(poster, user, size.equals("normal") ? Order.PosterSize.NORMAL : null, qty, address);
        orderRepository.save(order);
        
        model.addAttribute("orderNumber", order.getId());
        
        return "orderplaced";
    }

}
