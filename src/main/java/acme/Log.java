package acme;

import java.nio.file.Path;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 * 
 * @author Daniel Siviter
 * @since v1.0 [11 Oct 2019]
 *
 */
@MessageLogger(projectCode = "WS")
public interface Log extends BasicLogger {
	@LogMessage
	@Message(value = "Static resource requested: %s")
	void staticResource(Path path);
}
