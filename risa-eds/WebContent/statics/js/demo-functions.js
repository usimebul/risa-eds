var config = new CloudOauthConfig(); // Cloud PKI configuration
config.setHost("https://uat-cloud.govca.rw/cloud-service");
config.setClientId("local_eds_demo");
config.setRedirectUri("http://localhost:9190/callback.html");
config.setScope("read");

// get signature value in PDF from CloudPKI
function getP7DetachedMessageFromCloud() {
	var cloudoauth = new CloudOauth(config);

	var customCallback = function(result) {
		result = result.CP400;
		$("#textarea-p7-detached-message").val(result.p7SignHex);
		$("#textarea-certificate").val(result.certHex);
	}


	var plainText = $('#text-pdf-hash').val();
	cloudoauth
		.setApiCodes("CP400")
		.setPlainText(plainText, true)
		.call(customCallback);
}


// get PDF hash
function getPDFHash() {
	var data = {
		pdfFileName: $('#input-pdf-name').val(),
		signImgName: $('#input-sign-image-name').val(),
		unifiedDocID: $('#input-unified-doc-id').val(),
		docID: $('#input-doc-id').val(),
		signerID: $('#input-signer-id').val(),
		signerName: $('#input-signer-name').val(),
		reason: $('#input-reason').val(),
		locationIP: $('#input-location-ip').val(),
		signerEmail: $('#input-signer-email').val(),

		signatureVisible: $('#checkbox-signature-visible').prop('checked'),
		signDateVisible: $('#checkbox-sign-date-visible').prop('checked'),
		reasnVisible: $('#checkbox-reason-visible').prop('checked'),
		signerVisible: $('#checkbox-signer-visible').prop('checked'),
		docIDVisible: $('#checkbox-doc-id-visible').prop('checked'),
		chainValid: $('#checkbox-chain-validiation').prop('checked'),
	}

	if ($('#input-signature-field-name').val() !== "") {
		data.signatureFieldName = $('#input-signature-field-name').val();
	} else {
		data.width = $('#input-width').val();
		data.height = $('#input-height').val();
		http: // sims.signgate.com/app/survey/new
		data.offsetX = $('#input-offset-x').val();
		data.offsetY = $('#input-offset-y').val();
		data.page = $('#input-page').val();
	}

	data = JSON.stringify(data);
	$.ajax({
		url: "./getPDFHash",
		method: 'POST',
		data: data,
		dataType: 'text',
		contentType: 'text/plain',
		success: function(data, status, xhr) {
			data = JSON.parse(data);
			console.log(data);

			if (data.ERROR) {
				console.error(data.ERROR);
				alert(data.ERROR.message);
				return;
			}

			$('#text-pdf-hash').val(data.hash);
			$('#gend-signature-field-name').val(data.signautreFieldName);
		},

	});
}

function attachP7DetachedMessage() {
	var data = {
		pdfFileName: $('#input-pdf-name').val() + "_done.pdf",
		signatureFieldName: $('#gend-signature-field-name').val(),
		unifiedDocID: $('#input-unified-doc-id').val(),
		docID: $('#input-doc-id').val(),
		signerID: $('#input-signer-id').val(),
		p7Message: $('#textarea-p7-detached-message').val(),
		certData: $('#textarea-certificate').val(),
		chainValid: $('#checkbox-chain-validiation').prop('checked')
	}

	data = JSON.stringify(data);
	$.ajax({
		url: "./addDetachedSignature",
		method: 'POST',
		data: data,
		dataType: 'text',
		contentType: 'text/plain',
		success: function(data, status, xhr) {
			data = JSON.parse(data);

			if (data.ERROR) {
				console.error(data.ERROR);
				alert(data.ERROR.message);
				return;
			}

			alert(data.result);
		//	var temp = $('#input-pdf-name').val().split("/");
		//	var newFileName = temp[temp.length - 1] + "_done.pdf";
			var newFileName = $('#input-pdf-name').val() + "_done.pdf";
			var url = "./download/" + newFileName;

			var a = document.createElement("a");
			a.style.display = "none";
			document.body.appendChild(a);

			a.href = url;
			a.setAttribute("download", newFileName);
			a.click();

			window.URL.revokeObjectURL(a.href);
			document.body.removeChild(a);
		},

	});
}



function genSignedPDFbyCloudPKI() {
	
	var data = {
			pdfFileName: $('#input-pdf-name').val(),
			signImgName: $('#input-sign-image-name').val(),
			unifiedDocID: $('#input-unified-doc-id').val(),
			docID: $('#input-doc-id').val(),
			signerID: $('#input-signer-id').val(),
			signerName: $('#input-signer-name').val(),
			reason: $('#input-reason').val(),
			locationIP: $('#input-location-ip').val(),
			signerEmail: $('#input-signer-email').val(),

			signatureVisible: $('#checkbox-signature-visible').prop('checked'),
			signDateVisible: $('#checkbox-sign-date-visible').prop('checked'),
			reasnVisible: $('#checkbox-reason-visible').prop('checked'),
			signerVisible: $('#checkbox-signer-visible').prop('checked'),
			docIDVisible: $('#checkbox-doc-id-visible').prop('checked'),
			chainValid: $('#checkbox-chain-validiation').prop('checked'),
		}

		if ($('#input-signature-field-name').val() !== "") {
			data.signatureFieldName = $('#input-signature-field-name').val();
		} else {
			data.width = $('#input-width').val();
			data.height = $('#input-height').val();
			http: // sims.signgate.com/app/survey/new
			data.offsetX = $('#input-offset-x').val();
			data.offsetY = $('#input-offset-y').val();
			data.page = $('#input-page').val();
		}

		data = JSON.stringify(data);
		$.ajax({
			url: "./getPDFHash",
			method: 'POST',
			data: data,
			dataType: 'text',
			contentType: 'text/plain',
			success: function(data, status, xhr) {
				data = JSON.parse(data);
				console.log(data);

				if (data.ERROR) {
					console.error(data.ERROR);
					alert(data.ERROR.message);
					return;
				}

				$('#text-pdf-hash').val(data.hash);
				$('#gend-signature-field-name').val(data.signautreFieldName);
				
				
				var cloudoauth = new CloudOauth(config);

				var customCallback = function(result) {
					
					if(result.CP400.success){
						
						$("#textarea-p7-detached-message").val(result.p7SignHex);
						$("#textarea-certificate").val(result.certHex);
						
						var data = {
								pdfFileName: $('#input-pdf-name').val() + "_done.pdf",
								signatureFieldName: $('#gend-signature-field-name').val(),
								unifiedDocID: $('#input-unified-doc-id').val(),
								docID: $('#input-doc-id').val(),
								signerID: $('#input-signer-id').val(),
								p7Message: $('#textarea-p7-detached-message').val(),
								certData: $('#textarea-certificate').val(),
								chainValid: $('#checkbox-chain-validiation').prop('checked')
							}

							data = JSON.stringify(data);
							$.ajax({
								url: "./addDetachedSignature",
								method: 'POST',
								data: data,
								dataType: 'text',
								contentType: 'text/plain',
								success: function(data, status, xhr) {
									data = JSON.parse(data);

									if (data.ERROR) {
										console.error(data.ERROR);
										alert(data.ERROR.message);
										return;
									}

									alert(data.result);
									//var temp = $('#input-pdf-name').val().split("/");
									//var newFileName = temp[temp.length - 1] + "_done.pdf";
									var newFileName = $('#input-pdf-name').val() + "_done.pdf";
									var url = "./download/" + newFileName;

									var a = document.createElement("a");
									a.style.display = "none";
									document.body.appendChild(a);

									a.href = url;
									a.setAttribute("download", newFileName);
									a.click();

									window.URL.revokeObjectURL(a.href);
									document.body.removeChild(a);
								},

							});
							
							
						
					}else{
						alert(result.CP500.msg);
						return;
					}
					
					
					
				}


				var plainText = $('#text-pdf-hash').val();
				cloudoauth
					.setApiCodes("CP400")
					.setPlainText(plainText, true)
					.call(customCallback);
				
				
			},

		});
	
		
		
	
	
}




function sign() {
	// Set SigningData attributes.
	var data = {
		pdfFileName: $('#input-pdf-name').val(),
		signImgName: $('#input-sign-image-name').val(),
		unifiedDocID: $('#input-unified-doc-id').val(),
		docID: $('#input-doc-id').val(),
		signerID: $('#input-signer-id').val(),
		signerName: $('#input-signer-name').val(),
		reason: $('#input-reason').val(),
		locationIP: $('#input-location-ip').val(),
		signerEmail: $('#input-signer-email').val(),

		signatureVisible: $('#checkbox-signature-visible').prop('checked'),
		signDateVisible: $('#checkbox-sign-date-visible').prop('checked'),
		reasnVisible: $('#checkbox-reason-visible').prop('checked'),
		signerVisible: $('#checkbox-signer-visible').prop('checked'),
		docIDVisible: $('#checkbox-doc-id-visible').prop('checked'),
		chainValid: $('#checkbox-chain-validiation').prop('checked')
	}

	if ($('#input-signature-field-name').val() !== "") {
		data.signatureFieldName = $('#input-signature-field-name').val();
	} else {
		data.width = $('#input-width').val();
		data.height = $('#input-height').val();
		http: // sims.signgate.com/app/survey/new
		data.offsetX = $('#input-offset-x').val();
		data.offsetY = $('#input-offset-y').val();
		data.page = $('#input-page').val();
	}

	// Convert javascript object to JSON string.
	data = JSON.stringify(data);
	// Request to server
	$.ajax({
		url: "./sign",
		method: 'POST',
		data: data,
		dataType: 'text',
		contentType: 'text/plain',
		success: function(data, status, xhr) {
			data = JSON.parse(data);

			if (data.ERROR) {
				console.error(data.ERROR);
				alert(data.ERROR.message);
				return;
			}

			alert(data.result);
			//var temp = $('#input-pdf-name').val().split("/");

			//var newFileName = temp[temp.length - 1];
			
			//var url = "./download/" + newFileName;
			var url = "./download/" + $('#input-pdf-name').val();
			

			const a = document.createElement("a");
			a.style.display = "none";
			document.body.appendChild(a);

			// download it
			a.href = url;
			//a.setAttribute("download", newFileName);
			a.setAttribute("download", $('#input-pdf-name').val());
			a.click();

			window.URL.revokeObjectURL(a.href);
			document.body.removeChild(a);
		},

	});
}

function signByThree() {
	// Set SigningData attributes.
	var data = {
		pdfFileName: $('#input-pdf-name').val(),
		signImgName: $('#input-sign-image-name').val(),
		unifiedDocID: $('#input-unified-doc-id').val(),
		docID: $('#input-doc-id').val(),
		signerID: $('#input-signer-id').val(),
		signerName: $('#input-signer-name').val(),
		reason: $('#input-reason').val(),
		locationIP: $('#input-location-ip').val(),
		signerEmail: $('#input-signer-email').val(),

		signatureVisible: $('#checkbox-signature-visible').prop('checked'),
		signDateVisible: $('#checkbox-sign-date-visible').prop('checked'),
		reasnVisible: $('#checkbox-reason-visible').prop('checked'),
		signerVisible: $('#checkbox-signer-visible').prop('checked'),
		docIDVisible: $('#checkbox-doc-id-visible').prop('checked'),
		chainValid: $('#checkbox-chain-validiation').prop('checked')
	}

	if ($('#input-signature-field-name').val() !== "") {
		data.signatureFieldName = $('#input-signature-field-name').val();
	} else {
		data.width = $('#input-width').val();
		data.height = $('#input-height').val();
		data.offsetX = $('#input-offset-x').val();
		data.offsetY = $('#input-offset-y').val();
		data.page = $('#input-page').val();
	}

	// Convert javascript object to JSON string.
	data = JSON.stringify(data);
	// Request to server
	$.ajax({
		url: "./signByThree",
		method: 'POST',
		data: data,
		dataType: 'text',
		contentType: 'text/plain',
		success: function(data, status, xhr) {
			data = JSON.parse(data);

			if (data.ERROR) {
				console.error(data.ERROR);
				alert(data.ERROR.message);
				return;
			}

			alert(data.result);
			//var temp = $('#input-pdf-name').val().split("/");

			///var newFileName = temp[temp.length - 1];
			//var url = "./download/" + newFileName;
			var url = "./download/" + $('#input-pdf-name').val();

			const a = document.createElement("a");
			a.style.display = "none";
			document.body.appendChild(a);

			// download it
			a.href = url;
			//a.setAttribute("download", newFileName);
			a.setAttribute("download", $('#input-pdf-name').val());
			a.click();

			window.URL.revokeObjectURL(a.href);
			document.body.removeChild(a);
		},

	});
}


function timestamp() {
	// Set SigningData attributes.
	var data = {
		pdfFileName: $('#input-pdf-name').val(),
		signImgName: $('#input-tsa-image-name').val(),
		unifiedDocID: $('#input-unified-doc-id').val(),
		docID: $('#input-doc-id').val(),
		signerID: $('#input-signer-id').val(),
		signerName: $('#input-signer-name').val(),
		reason: $('#input-reason').val(),
		locationIP: $('#input-location-ip').val(),
		signerEmail: $('#input-signer-email').val(),

		signatureVisible: $('#checkbox-signature-visible').prop('checked'),
		signDateVisible: $('#checkbox-sign-date-visible').prop('checked'),
		reasnVisible: $('#checkbox-reason-visible').prop('checked'),
		signerViszible: $('#checkbox-signer-visible').prop('checked'),
		docIDVisible: $('#checkbox-doc-id-visible').prop('checked'),
		chainValid: $('#checkbox-chain-validiation').prop('checked')
	}

	if ($('#input-signature-field-name').val() !== "") {
		data.signatureFieldName = $('#input-signature-field-name').val();
	} else {
		data.width = $('#input-width').val();
		data.height = $('#input-height').val();
		data.offsetX = $('#input-offset-x').val();
		data.offsetY = $('#input-offset-y').val();
		data.page = $('#input-page').val();
	}

	// Convert javascript object to JSON string.
	data = JSON.stringify(data);

	// Request to server
	$.ajax({
		url: "./timestamp",
		method: 'POST',
		data: data,
		dataType: 'text',
		contentType: 'text/plain',
		success: function(data, status, xhr) {
			data = JSON.parse(data);

			if (data.ERROR) {
				console.error(data.ERROR);
				alert(data.ERROR.message);
				return;
			}

			alert(data.result);

			//var temp = $('#input-pdf-name').val().split("/");
			//var temp = $('#input-pdf-name').val();

			//var newFileName = temp[temp.length - 1];
			//var url = "./download/" + newFileName
			var url = "./download/" + $('#input-pdf-name').val();

			// download
			const a = document.createElement("a");
			a.style.display = "none";
			document.body.appendChild(a);

			a.href = url;
			//a.setAttribute("download", newFileName);
			a.setAttribute("download", $('#input-pdf-name').val());
			a.click();

			window.URL.revokeObjectURL(a.href);
			document.body.removeChild(a);
		},

	});
}

function makeEmptyField() {
	// Set SigningData attributes.
	var data = {
		pdfFileName: $('#input-pdf-name').val(),
		unifiedDocID: $('#input-unified-doc-id').val(),
		signerID: $('#input-signer-id').val()
	}

	data.width = $('#input-width').val();
	data.height = $('#input-height').val();
	data.offsetX = $('#input-offset-x').val();
	data.offsetY = $('#input-offset-y').val();
	data.page = $('#input-page').val();

	// If signature field name is null, it automatically genrated as KICA_EDS_{n}
	// n = The number of unsigned signature fields.
	if ($('#input-signature-field-name').val() !== "") {
		data.signatureFieldName = $('#input-signature-field-name').val();
	}

	// Convert javascript object to JSON string.
	data = JSON.stringify(data);

	// Request to server
	$.ajax({
		url: "./makeEmptyField",
		method: 'POST',
		data: data,
		dataType: 'text',
		contentType: 'text/plain',
		success: function(data, status, xhr) {
			data = JSON.parse(data);

			if (data.ERROR) {
				console.error(data.ERROR);
				alert(data.ERROR.message);
				return;
			}

			// Get generated unsigned signature field name.
			$('#text-signature-field-name-result').val(data.signautreFieldName);
			alert(data.result);


			// download it
			//var temp = $('#input-pdf-name').val().split("/");


			//var newFileName = temp[temp.length - 1];
			//var url = "./download/" + newFileName;
			var url = "./download/" + $('#input-pdf-name').val();

			const a = document.createElement("a");
			a.style.display = "none";
			document.body.appendChild(a);

			// Set the HREF to a Blob representation of the data to be
			// downloaded
			a.href = url;

			// Use download attribute to set set desired file name
			//a.setAttribute("download", newFileName);
			a.setAttribute("download", $('#input-pdf-name').val());

			// Trigger the download by simulating click
			a.click();

			// Cleanup
			window.URL.revokeObjectURL(a.href);
			document.body.removeChild(a);
		},

	});
}