package sociality.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import sociality.server.audit.Audit;
import sociality.server.model.User;
import sociality.server.resolvers.JsonArg;
import sociality.server.services.UserService;

@Controller
public class RegisterController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	@Audit(action = "register")
	public User register(@JsonArg String username, @JsonArg String password, @JsonArg String firstName,
			@JsonArg String lastName) {
		User user = userService.createRegularUser(username, password, firstName, lastName);
		return user;
	}

}
