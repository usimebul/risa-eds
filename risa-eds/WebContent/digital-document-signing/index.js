// set signature position for authorization document.
function _setAuthorization() {
	$("#offsetX").val(120);
	$("#offsetY").val(85);
	$("#width").val(120);
	$("#height").val(55);
	$("#page").val(1);
}

// set signature position for confirmation document.
function _setConfirmation() {
	$("#offsetX").val(120);
	$("#offsetY").val(85);
	$("#width").val(120);
	$("#height").val(55);
	$("#page").val(1);
}

_setAuthorization(); // defaultly authorization selected

// event handler for selecting options. 
$("#document-type").on("change", function(e) {
	$(".position").addClass('d-none');
	switch (this.value) {
		case 'authorization':
			_setAuthorization();
			break;
		case 'confirmation':
			_setConfirmation();
			break;
		default:
			// to show position inputs for manual setting.
			$(".position").removeClass('d-none');
			break;
	}
});

// event handler for click choose signature image button;
// it opens file explorer to select a image file.
$("#choose-signature-image").on("click", function(e) {
	$("#image-file").trigger('click');
})

// event handler for click drop-area
// it opens file explorer to select a pdf file.
$("#drop-area").on("click", function(e) {
	$("#pdf-file").trigger('click');
})

$("#pdf-file").on("change", function(e) {
	if (this.files.length === 0) {
		$(".no-file-wrapper").removeClass("d-none");
		$(".file-wrapper").addClass("d-none");
		return;
	}

	$("#form-pdf-file").trigger('submit');
})

$("#image-file").on("change", function(e) {
	if (this.files.length === 0) {
		$(".no-file-wrapper").removeClass("d-none");
		$(".file-wrapper").addClass("d-none");
		return;
	}

	$("#form-image-file").trigger('submit');
})

// event handler for drop a file at drop-area
$("#drop-area")[0].addEventListener('dragover', function(e) { e.preventDefault(); })
$("#drop-area")[0].addEventListener('dragleave', function(e) { e.preventDefault(); })
$("#drop-area")[0].addEventListener('drop', function(e) {
	e.preventDefault();
	if (e.dataTransfer.files.length > 1) {
		alert("Please upload one.");
		$(".no-file-wrapper").removeClass("d-none");
		$(".file-wrapper").addClass("d-none");
		return;
	}

	if (e.dataTransfer.files[0].name.indexOf("pdf") === -1) {
		alert("Please upload a PDF file.");
		$(".no-file-wrapper").removeClass("d-none");
		$(".file-wrapper").addClass("d-none");
		return;
	}

	$("#pdf-file")[0].files = e.dataTransfer.files;
	$("#form-pdf-file").trigger('submit');
})

var fileName;
$("#form-pdf-file").on("submit", function(e) {
	e.preventDefault();
	if ($("#pdf-file")[0].files.length === 0) {
		alert("Please browser or drag&drop a pdf file.");
		return;;
	}

	$.ajax({
		type: "POST",
		enctype: 'multipart/form-data',
		url: "/upload",
		data: new FormData(this),
		processData: false,
		contentType: false,
		cache: false,
		timeout: 600000,
		success: function(data) {
			if (data.result !== "uploaded") {
				alert("The file has been failed.");
				return;
			}
			// show file name
			$(".no-file-wrapper").addClass("d-none");
			$(".file-wrapper").removeClass("d-none");
			$(".file-wrapper .text").html($("#pdf-file")[0].files[0].name);
			fileName = $("#pdf-file")[0].files[0].name;

		},
		error: function(e) {
			console.log("ERROR : ", e);
			alert("ERROR : ", e);
		}
	});
})

var imageFileName;
$("#form-image-file").on("submit", function(e) {
	e.preventDefault();
	if ($("#image-file")[0].files.length === 0) {
		alert("Please choose a image file representing signature.");
		return;;
	}

	$.ajax({
		type: "POST",
		enctype: 'multipart/form-data',
		url: "/upload",
		data: new FormData(this),
		processData: false,
		contentType: false,
		cache: false,
		timeout: 600000,
		success: function(data) {
			if (data.result !== "uploaded") {
				alert("The file has been failed.");
				return;
			}
			imageFileName = $("#image-file")[0].files[0].name;
		},
		error: function(e) {
			console.log("ERROR : ", e);
			alert("ERROR : ", e);
		}
	});
})


// event handler for click sign document button;
$("#sign-document").on("click", function(e) {
	if (!$("#signerName").val()) {
		alert("Please ennter the name");
		return;
	}

	if ($("#pdf-file")[0].files.length === 0) {
		alert("Please upload a pdf file");
		return;
	}

	if ($("#image-file")[0].files.length === 0) {
		alert("Please upload an image file");
		return;
	}

	var signatureFieldName;
	// 3. add detached signature aftert singing
	function _addDetachedSignature(result) {
		$("#spinner").removeClass('d-none');

		var props = {
			signatureFieldName: signatureFieldName,
			fileName: fileName,
			p7Message: result.CP400.p7SignHex,
			certData: result.CP400.certHex,
		}

		$.ajax({
			url: "/addDetachedSignature2",
			method: 'POST',
			data: JSON.stringify(props),
			dataType: 'text',
			contentType: 'text/plain',
			success: function(data) {
				$("#spinner").addClass('d-none');
				console.log('Add Signature Result:' + data);
				data = JSON.parse(data);
				if (data.ERROR) {
					console.error(data.ERROR);
					alert(data.ERROR.message);
					return;
				}

				// 4. download signed PDF file
				var url = "/download/" + fileName;
				var a = document.createElement("a");
				a.style.display = "none";
				document.body.appendChild(a);

				a.href = url;
				a.setAttribute("download", fileName);
				a.click();

				window.URL.revokeObjectURL(a.href);
				document.body.removeChild(a);
			},
			error: function(error) {
				alert("Server Error: " + error);
				$("#spinner").addClass('d-none');
			},
		});
	}

	// 1. get hash value of the pdf file
	var props = {
		width: $("#width").val(),
		height: $("#height").val(),
		offsetX: $("#offsetX").val(),
		offsetY: $("#offsetY").val(),
		page: $("#page").val(),
		fileName: fileName,
		imageFileName: imageFileName,
		signerName: $("#signerName").val()
	}

	$.ajax({
		url: "/getPDFHash2",
		method: 'POST',
		data: JSON.stringify(props),
		dataType: 'text',
		contentType: 'text/plain',
		success: function(data) {
			console.log('Get PDF Hash Result:' + data);
			data = JSON.parse(data);
			if (data.ERROR) {
				console.error(data.ERROR);
				alert(data.ERROR.message);
				return;
			}
			signatureFieldName = data.signautreFieldName;

			// 2. set cloud pki configuration.
			var config = new CloudOauthConfig();
			config.setHost("https://uat-cloud.govca.rw/cloud-service");
			config.setClientId("local_eds_demo");
			config.setRedirectUri("http://localhost:9190/callback.html");
			config.setScope("read");

			var cloudoauth = new CloudOauth(config);
			cloudoauth
				.setApiCodes("CP400")
				.setPlainText(data.hash, true)
				.call(_addDetachedSignature);
		},
		error: function(error) {
			alert("Server Error: " + error);
		},
	});
});





