package org.talend.msg.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.datatypes.AttachmentChunks;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgMailUtil {

	private String outAttachmentPath;
	private MAPIMessage msg;
	private Logger log;
	private String position;

	public MsgMailUtil() {
	}
	
	private enum Level {
		DEBUG,
		INFO,
		TRACE,
		FATAL,
		WARN,
		ERROR
	}

	public MsgMailUtil(String fileName, String outAttachmentPath)
			throws IOException {
		this.msg = new MAPIMessage(fileName);
		this.outAttachmentPath = outAttachmentPath;
	}

	public void activeLog(String loggerName, String position) {
		this.log = LoggerFactory.getLogger(loggerName);
		this.position = position;
	}

	public void getAttachments() throws IOException {
		AttachmentChunks[] attachments = msg.getAttachmentFiles();
		if (attachments.length > 0) {
			File d = new File(outAttachmentPath);
			if (!d.exists()) {
				processLog(Level.DEBUG,
						"Specified attachments' export directory doesn't exist");
				d.mkdirs();// Create output path if it not exist.
				processLog(Level.DEBUG, "Directory " + d.getAbsolutePath()
						+ " was created successfully");
			}
			for (AttachmentChunks attachment : attachments) {
				processAttachment(attachment, d);
			}
		}
	}

	public void processAttachment(AttachmentChunks attachment, File dir)
			throws IOException {
		String fileName = attachment.getAttachFileName().toString();
		if (attachment.getAttachLongFileName() != null) {
			fileName = getUniqueFileName(attachment.getAttachLongFileName().toString(), dir);
		}

		File attachedFile = new File(dir, fileName);
		try(OutputStream fileOut = new FileOutputStream(attachedFile)) {
			processLog(Level.INFO, "Exporting attachment file :" + fileName);
			processLog(Level.INFO,
					"File location:" + attachedFile.getAbsolutePath());

			fileOut.write(attachment.getEmbeddedAttachmentObject());

			processLog(Level.INFO, "Export successfully");
		}
	}

	private String getUniqueFileName(String fileName,  File dir){

		int num = 1;
		final String ext = getFileExtension(fileName);
		final String name = getFileName(fileName);
		File file = new File(dir, fileName);
		while (file.exists()) {
			num++;
			file = new File(dir, name + "_" + num + ext);
		}
		return file.getName();
	}

	public static String getFileExtension(final String path) {
		if (path != null && path.lastIndexOf('.') != -1) {
			return path.substring(path.lastIndexOf('.'));
		}
		return null;
	}

	public static String getFileName(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}

	public Object processMessage(String mailPart) throws ChunkNotFoundException {
		if ("From".equalsIgnoreCase(mailPart)) {
			return msg.getDisplayFrom();
		} else if ("To".equalsIgnoreCase(mailPart)) {
			return msg.getDisplayTo();
		} else if ("CC".equalsIgnoreCase(mailPart)) {
			return msg.getDisplayCC();
		} else if ("BCC".equalsIgnoreCase(mailPart)) {
			return msg.getDisplayBCC();
		} else if ("Subject".equalsIgnoreCase(mailPart)) {
			return msg.getSubject();
		} else if ("Body".equalsIgnoreCase(mailPart)) {
			return msg.getTextBody();
		} else if ("ConversationTopic".equalsIgnoreCase(mailPart)) {
			return msg.getConversationTopic();
		} else if ("HtmlBody".equalsIgnoreCase(mailPart)) {
			return msg.getHtmlBody();
		} else if ("MessageDate".equalsIgnoreCase(mailPart)) {
			java.util.Calendar messageDate = msg.getMessageDate();
			return messageDate.getTime();
		} else if ("RecipientAddress".equalsIgnoreCase(mailPart)) {
			return msg.getRecipientEmailAddress();
		} else if ("RecipientAddressList".equalsIgnoreCase(mailPart)) {
			return msg.getRecipientEmailAddressList();
		} else if ("RecipientNames".equalsIgnoreCase(mailPart)) {
			return msg.getRecipientNames();
		} else if ("RecipientNamesList".equalsIgnoreCase(mailPart)) {
			return msg.getRecipientNamesList();
		} else if ("RtfBody".equalsIgnoreCase(mailPart)) {
			return msg.getRtfBody();
		} else {
			return null;
		}
	}

	private void processLog(Level level, String message) {
		message = position + " - " + message;
		if (this.log != null) {
			switch (level) {
			case TRACE:
				log.trace(message);
				break;
			case ERROR:
				log.error(message);
				break;
			case INFO:
				log.info(message);
				break;
			case WARN:
				log.warn(message);
				break;
			default:
				break;
			}
		}
	}

	public String getOutAttachmentPath() {
		return outAttachmentPath;
	}

	public void setOutAttachmentPath(String outAttachmentPath) {
		this.outAttachmentPath = outAttachmentPath;
	}

	public MAPIMessage getMsg() {
		return msg;
	}

	public void setMsg(MAPIMessage msg) {
		this.msg = msg;
	}

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

}