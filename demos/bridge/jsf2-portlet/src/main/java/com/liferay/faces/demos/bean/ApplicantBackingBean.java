/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.faces.demos.bean;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UICommand;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.liferay.faces.bridge.component.HtmlInputFile;
import com.liferay.faces.bridge.component.UploadedFile;
import com.liferay.faces.bridge.logging.Logger;
import com.liferay.faces.bridge.logging.LoggerFactory;
import com.liferay.faces.demos.dto.City;
import com.liferay.faces.demos.util.FacesMessageUtil;


/**
 * This is a JSF backing managed-bean for the applicant.xhtml composition.
 *
 * @author  "Neil Griffin"
 */
@ManagedBean(name = "applicantBackingBean")
@ViewScoped
public class ApplicantBackingBean implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 2947548873495692163L;

	// Logger
	private static final transient Logger logger = LoggerFactory.getLogger(ApplicantBackingBean.class);

	// Injections
	@ManagedProperty(value = "#{applicantModelBean}")
	private transient ApplicantModelBean applicantModelBean;
	@ManagedProperty(value = "#{listModelBean}")
	private transient ListModelBean listModelBean;

	// JavaBeans Properties for UI
	private boolean commentsRendered = false;
	private boolean fileUploaderRendered = false;

	private transient HtmlInputFile attachment1;
	private transient HtmlInputFile attachment2;
	private transient HtmlInputFile attachment3;

	public void addAttachment(ActionEvent actionEvent) {
		fileUploaderRendered = true;
	}

	public void deleteUploadedFile(ActionEvent actionEvent) {

		UICommand uiCommand = (UICommand) actionEvent.getComponent();
		String fileId = (String) uiCommand.getValue();

		try {
			List<UploadedFile> uploadedFiles = applicantModelBean.getUploadedFiles();

			UploadedFile uploadedFileToDelete = null;

			for (UploadedFile uploadedFile : uploadedFiles) {

				if (uploadedFile.getId().equals(fileId)) {
					uploadedFileToDelete = uploadedFile;

					break;
				}
			}

			if (uploadedFileToDelete != null) {
				File file = new File(uploadedFileToDelete.getAbsolutePath());
				file.delete();
				uploadedFiles.remove(uploadedFileToDelete);
				logger.debug("Deleted file=[{0}]", file);
			}
		}
		catch (Exception e) {
			logger.error(e);
		}
	}

	public void postalCodeListener(ValueChangeEvent valueChangeEvent) {

		try {
			String newPostalCode = (String) valueChangeEvent.getNewValue();
			City city = listModelBean.getCityByPostalCode(newPostalCode);

			if (city != null) {
				applicantModelBean.setAutoFillCity(city.getCityName());
				applicantModelBean.setAutoFillProvinceId(city.getProvinceId());
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			FacesMessageUtil.addGlobalUnexpectedErrorMessage(FacesContext.getCurrentInstance());
		}
	}

	public String submit() {

		if (logger.isDebugEnabled()) {
			logger.debug("firstName=" + applicantModelBean.getFirstName());
			logger.debug("lastName=" + applicantModelBean.getLastName());
			logger.debug("emailAddress=" + applicantModelBean.getEmailAddress());
			logger.debug("phoneNumber=" + applicantModelBean.getPhoneNumber());
			logger.debug("dateOfBirth=" + applicantModelBean.getDateOfBirth());
			logger.debug("city=" + applicantModelBean.getCity());
			logger.debug("provinceId=" + applicantModelBean.getProvinceId());
			logger.debug("postalCode=" + applicantModelBean.getPostalCode());
			logger.debug("comments=" + applicantModelBean.getComments());

			List<UploadedFile> uploadedFiles = applicantModelBean.getUploadedFiles();

			for (UploadedFile uploadedFile : uploadedFiles) {
				logger.debug("uploadedFile=[{0}]", uploadedFile.getName());
			}
		}

		// Delete the uploaded files.
		try {
			List<UploadedFile> uploadedFiles = applicantModelBean.getUploadedFiles();

			for (UploadedFile uploadedFile : uploadedFiles) {
				File file = new File(uploadedFile.getAbsolutePath());
				file.delete();
				logger.debug("Deleted file=[{0}]", file);
			}

			// Store the applicant's first name in JSF 2 Flash Scope so that it can be picked up
			// for use inside of confirmation.xhtml
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.getExternalContext().getFlash().put("firstName", applicantModelBean.getFirstName());

			applicantModelBean.clearProperties();

			return "success";

		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			FacesMessageUtil.addGlobalUnexpectedErrorMessage(FacesContext.getCurrentInstance());

			return "failure";
		}
	}

	public void toggleComments(ActionEvent actionEvent) {
		commentsRendered = !commentsRendered;
	}

	public void uploadAttachments(ActionEvent actionEvent) {

		int nextId = 0;
		List<UploadedFile> uploadedFiles = applicantModelBean.getUploadedFiles();
		int totalUploadedFiles = uploadedFiles.size();

		if (totalUploadedFiles > 0) {
			nextId = Integer.parseInt(uploadedFiles.get(totalUploadedFiles - 1).getId());
			nextId++;
		}

		UploadedFile uploadedFile1 = attachment1.getUploadedFile();

		if (uploadedFile1 != null) {
			uploadedFile1.setId(Integer.toString(nextId++));
			uploadedFiles.add(uploadedFile1);
			logger.debug("uploadedFile1=[{0}]", uploadedFile1.getName());
		}

		UploadedFile uploadedFile2 = attachment2.getUploadedFile();

		if (uploadedFile2 != null) {
			uploadedFile2.setId(Integer.toString(nextId++));
			uploadedFiles.add(uploadedFile2);
			logger.debug("uploadedFile2=[{0}]", uploadedFile2.getName());
		}

		UploadedFile uploadedFile3 = attachment3.getUploadedFile();

		if (uploadedFile3 != null) {
			uploadedFile3.setId(Integer.toString(nextId++));
			uploadedFiles.add(uploadedFile3);
			logger.debug("uploadedFile3=[{0}]", uploadedFile3.getName());
		}

		fileUploaderRendered = false;
	}

	public void setApplicantModelBean(ApplicantModelBean applicantModelBean) {

		// Injected via @ManagedProperty annotation
		this.applicantModelBean = applicantModelBean;
	}

	public HtmlInputFile getAttachment1() {
		return attachment1;
	}

	public void setAttachment1(HtmlInputFile attachment1) {
		this.attachment1 = attachment1;
	}

	public HtmlInputFile getAttachment2() {
		return attachment2;
	}

	public void setAttachment2(HtmlInputFile attachment2) {
		this.attachment2 = attachment2;
	}

	public HtmlInputFile getAttachment3() {
		return attachment3;
	}

	public void setAttachment3(HtmlInputFile attachment3) {
		this.attachment3 = attachment3;
	}

	public void setCommentsRendered(boolean commentsRendered) {
		this.commentsRendered = commentsRendered;
	}

	public boolean isCommentsRendered() {
		return commentsRendered;
	}

	public boolean isFileUploaderRendered() {
		return fileUploaderRendered;
	}

	public void setListModelBean(ListModelBean listModelBean) {

		// Injected via @ManagedProperty annotation
		this.listModelBean = listModelBean;
	}
}
