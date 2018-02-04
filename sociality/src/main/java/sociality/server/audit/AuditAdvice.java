package sociality.server.audit;

import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sociality.server.model.User;

@Aspect
@Component
public class AuditAdvice {

	@Autowired
	private AuditService auditService;

	@Before("@annotation(auditAnnotation)")
	public void auditActions(JoinPoint joinPoint, Audit auditAnnotation) {
		if (auditAnnotation.action().equals("register")) {
			List<Object> args = Arrays.asList(joinPoint.getArgs());
			auditService.auditRegister(args);
			return;
		}
		User u = (User) joinPoint.getArgs()[joinPoint.getArgs().length - 1];
		auditService.audit(auditAnnotation.action(), u);
	}

}
