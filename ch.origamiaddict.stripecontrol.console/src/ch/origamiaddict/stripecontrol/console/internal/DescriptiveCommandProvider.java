package ch.origamiaddict.stripecontrol.console.internal;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.osgi.framework.console.CommandProvider;

public abstract class DescriptiveCommandProvider implements CommandProvider {

	private static final Pattern CMD_PATTERN = Pattern.compile("^_(.*)");
	private String help = null;

	public String getHelp() {
		if (null == help) {
			help = buildHelp();
		}
		return help;
	}

	private String buildHelp() {
		StringBuilder helpBuff = new StringBuilder();
		
		helpBuff.append(getTitle(this));

		for (Method m : this.getClass().getMethods()) {
			if (methodIsCmd(m)) {
				if (0 != helpBuff.length()) {
					helpBuff.append("\n");
				}
				helpBuff.append(getDocumentation(m));
			}
		}
		return helpBuff.toString();
	}

	private boolean methodIsCmd(Method m) {
		return CMD_PATTERN.matcher(m.getName()).matches();
	}

	private String getTitle(Object c) {
		CmdDescr title = c.getClass().getAnnotation(CmdDescr.class);
		StringBuilder docTitle = new StringBuilder();
		if (title != null) {
			docTitle.append("---");
			docTitle.append(title.title());
			docTitle.append("---");
		}
		return docTitle.toString();
	}

	private String getDocumentation(Method m) {
		StringBuilder methodHelp = new StringBuilder();

		Matcher matcher = CMD_PATTERN.matcher(m.getName());
		if (matcher.matches()) {
			methodHelp.append("\t" + matcher.group(1));

			CmdDescr description = m.getAnnotation(CmdDescr.class);

			if (null != description) {
				methodHelp.append(" - " + description.description());
			}
		}
		return methodHelp.toString();
	}
}