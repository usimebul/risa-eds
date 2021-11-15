
// default value
$("#input-pdf-name").val("to_be_signed_PDF.pdf");
$("#input-sign-image-name").val("to_be_SignerSignatureImage.jpg");
$("#input-tsa-image-name").val("to_be_TimeStampImage.jpg");

$(".custom-file-input").on(
	"change",
	function() {
		var fileName = $(this).val().split("\\").pop();
		$(this).siblings(".custom-file-label").addClass(
			"selected").html(fileName);
	});


// file uploading event
$("#pdf-file-submit").click(function(event) {
	event.preventDefault();

	if ($('#input-file').val() === "") {
		alert('Choose a file.');
		return;
	}

	// Get form
	var form = $('#form-pdf-file')[0];
	// Create an FormData object
	var data = new FormData(form);

	// If you want to add an extra field for the FormData
	data.append("CustomField", "This is some extra data, testing");

	$.ajax({
		type: "POST",
		enctype: 'multipart/form-data',
		url: "./upload",
		data: data,
		processData: false,
		contentType: false,
		cache: false,
		timeout: 600000,
		success: function(data) {
			if (data.result === "uploaded") {
				// disabled the submit button
				$("#pdf-file-submit").prop("disabled", true);
				$("#input-pdf-file").prop("disabled", true);

				$('#input-pdf-name').val($("#label-pdf-file-name").html());

				alert("The file has been uploaded.");
				return;
			}

			alert("The file has been failed.");
		},
		error: function(e) {
			console.log("ERROR : ", e);
			alert("ERROR : ", e);
		}
	});
});

// 파일 업로드 이벤트 처리
$("#sign-image-file-submit").click(function(event) {
	event.preventDefault();

	if ($('#input-sign-image-file').val() === "") {
		alert('Choose a file.');
		return;
	}

	// Get form
	var form = $('#form-sign-img-file')[0];
	// Create an FormData object
	var data = new FormData(form);

	// If you want to add an extra field for the FormData
	data.append("CustomField", "This is some extra data, testing");

	$.ajax({
		type: "POST",
		enctype: 'multipart/form-data',
		url: "./upload",
		data: data,
		processData: false,
		contentType: false,
		cache: false,
		timeout: 600000,
		success: function(data) {
			if (data.result === "uploaded") {
				// disabled the submit button
				$("#sign-image-file-submit").prop("disabled", true);
				$("#input-sign-image-file").prop("disabled", true);

				$('#input-sign-image-name').val($("#label-sign-image-fil-name").html());

				alert("The file has been uploaded.");
				return;
			}

			alert("The file has been failed.");
		},
		error: function(e) {
			console.log("ERROR : ", e);
			alert("ERROR : ", e);
		}
	});
});

// 파일 업로드 이벤트 처리
$("#tsa-img-file-submit").click(function(event) {
	event.preventDefault();

	if ($('#input-tsa-img-file').val() === "") {
		alert('Choose a file.');
		return;
	}

	// Get form
	var form = $('#form-tsa-img-file')[0];
	// Create an FormData object
	var data = new FormData(form);

	// If you want to add an extra field for the FormData
	data.append("CustomField", "This is some extra data, testing");

	$.ajax({
		type: "POST",
		enctype: 'multipart/form-data',
		url: "./upload",
		data: data,
		processData: false,
		contentType: false,
		cache: false,
		timeout: 600000,
		success: function(data) {
			if (data.result === "uploaded") {
				// disabled the submit button
				$("#tsa-img-file-submit").prop("disabled", true);
				$("#input-tsa-img-file").prop("disabled", true);

				$('#input-tsa-image-name').val($("#label-tsa-img-file-name").html());

				alert("The file has been uploaded.");
				return;
			}

			alert("The file has been failed.");
		},
		error: function(e) {
			console.log("ERROR : ", e);
			alert("ERROR : ", e);
		}
	});
});

