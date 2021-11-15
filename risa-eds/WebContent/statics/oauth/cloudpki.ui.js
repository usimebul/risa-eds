var cloudStyle = '';

var KicaCloudUIEvent = {
		certificateSelect : function(t, type){
			$(t).addClass("active").siblings("tr").removeClass("active");
		},
		radioSelect : function(t){
			$(t).find("input[type='radio']").prop("checked",true);
		}
}
var KicaCloudLoading = function(){
	var loading = this;
	var html = '<div id="kicaCloudLoadingWrap">';
	html += '<div id="kicaCloudDialogLoadingTable">';
	html += '<div id="kicaCloudDialogLoadingTableCell">';
	html += '<div class="lds-spinner"><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div></div>';		
	html += "</div>";
	html += "</div>";
	html += "</div>";
	this.html = $(html);
	this.isAppended = false;
	this.isSuccess = false;
	this.init = function(){
		if(!loading.isAppended)
			loading.html.appendTo("body");
		loading.isAppended = true;
		loading.html.show();
		
	}
	
	this.destroy = function(){
		loading.html.remove();
		loading.isAppended = false;
	}
	return loading;
}
var KicaCloudDialog = function(){
	var dialog = this;
	var dialogBtnClick;
	var html = '<div id="kicaCloudDialogWrap">';
	html += '<div id="kicaCloudDialogTable">';
	html += '<div id="kicaCloudDialogTableCell">';
	html += '<div id="kicaCloudDialog">';
	
	html += '<div id="kicaCloudDialogHeader"></div>';
	
	html += '<div id="kicaCloudDialogBody"></div>';
	
	html += '<div id="kicaCloudDialogFooter">';
	html += '<button type="button">Confirm</button>';
	html += "</div>";
		
	html += "</div>";
	html += "</div>";
	html += "</div>";
	html += "</div>";
	this.html = $(html);
	
	this.destroyInterval;
	this.isAppended = false;
	this.isSuccess = false;
	
	this.init = function(title, msg, destroyUI){
		if(!dialog.isAppended)
			dialog.html.appendTo("body");
		dialog.isSuccess = title == "SUCCESS" ? true : false;
		dialog.isAppended = true;
		dialog.html.find("#kicaCloudDialogHeader").text(title ? title : "INFO");
		dialog.html.find("#kicaCloudDialogBody").text(msg ? msg : "No message.");
		dialogBtnClick = function(){
			dialog.destroy();
			if(destroyUI)
				$("#kicaCloudControlWrap").remove();
				
		}
		dialog.html.find("#kicaCloudDialogFooter button").on("click",dialogBtnClick);
		dialog.html.find("#kicaCloudDialogFooter button").focus();
		dialog.html.show();
		
	}
	
	this.destroy = function(title, msg){
		try{
			dialog.html.addClass("dialog--hide");
			if(dialog.destroyInterval){
				clearTimeout(dialog.destroyInterval);
			}
			dialog.destroyInterval = setTimeout(function(){
				dialog.html.remove();
				dialog.html.removeClass("dialog--hide");
			},250)	
		}catch(e){
			dialog.html.remove();
			dialog.html.removeClass("dialog--hide");
		}
		if(dialog.isSuccess){
			$("#kicaCloudControlWrap").fadeOut(250, function(){
				$(this).remove();
			})
		}
		dialog.isAppended = false;
	}
	return dialog;
}
var KicaCloudUI = function(){
	var self = this;
	self.isGenerate = false,
	self.opt = {},
	self.wrap = null;
	
	var htmlElements = {
		generate : function(title, type){
			return $(this.common(title, type));
		},
		common : function(title, type){
			var html = '<div id="kicaCloudControlWrap" '+(type == "LOGINONLY" ? "style='display:none !important;'" : "")+'>';
			html += '<input type="hidden" id="cloudPKI_access_token">';
			html += '<input type="hidden" id="cloudPKI_access_type" value="'+type+'">';
			html += '<div class="cloudTableWrap">';
			html += '<div class="cloudTableTd">';
			html += '<div id="kicaCloudUiInner">';
			html += '<form id="kicaCloudInitFrm" onsubmit="return false;">';
			html += '<h1 id="cloudTitle">';
			html += title;
			html += '</h1>';
			
			html += '<div id="cloudContentWrap">';
//			if(descriptionText[type] !== "undefined"){
//				html += '<p class="cloudDescription">'+(descriptionText[type])+'</p>';
//			}
			html += '<p class="titIcon"><span>Select a certificate</span></p>';
			html += '<div id="certificateTableWrap">';
			html += '<table id="certificateTable">';
			html += '<thead>';
			html += '<tr>';
			html += '<th class="ccName">Name</th>';
			html += '<th class="ccPolicy">Policy</th>';
			html += '<th class="ccExpire">Expiration</th>';
			html += '<th class="ccAuthority">Authority</th>';
			html += '</tr>';
			html += '</thead>';
			html += '<tbody>';
			html += '</tbody>';
			html += '</table>';
			html += '</div>'; // #certificateTableWrap
			if(type !== "CERTLIST"){
				html += '<div id="certificatePasswordWrap">';
				html += '<label class="titIcon"><span>Enter the certificate '+(type == "CHANGEPASSWORD" ? 'old' : '')+' password.</span></label>';
				html += '<input autocomplete="off" type="password" id="certificatePasswordInput" maxlength="60" name="cert_pw" placeholder="You have only 3 times chances to enter your correct password.">';
				html += '<span class="cloudPasswordAlert">If you have lost your certificate password, you have to apply for certificate re-issuance at the front system.</span>';
				html += '</div>';
			}
			html += '<div id="certificateButtonWrap">';
			if(type !== "CERTLIST"){
				html += '<button type="button" id="cloudui-confirm-btn">Confirm</button>';
			}
			html += '<button type="button" id="cloudui-cancel-btn">Cancel</button>';
			html += '</div>'; 
			
			html += '</div>'; // #cloudContentWrap
			
			html += '<div id="cloudActiveTrigger"></div>'
			html += '</form>';
			html += '</div>'; // #kicaCloudUiInner
			html += '</div>'; // .cloudTableTd
			html += '</div>'; // .cloudTableWrap
			html += '</div>'; // #kicaCloudControlWrap
			return html;
		}
	};
	self.formatDate = function(date) {
	    var d = new Date(date),
	        month = '' + (d.getMonth() + 1),
	        day = '' + d.getDate(),
	        year = d.getFullYear();

	    if (month.length < 2) 
	        month = '0' + month;
	    if (day.length < 2) 
	        day = '0' + day;
	    return [year, month, day].join('-');
	}
	self.init = function(type){
		var evt = this;
		evt.type = type;
		if(self.isGenerate){
			evt.destroy();
		}
		
		evt.loadCss = function(){
			var head = document.head || document.getElementsByTagName('head')[0];
		    var style = document.createElement('style');
		    head.appendChild(style);
		    style.id = "cloudStyleSheet;"
		    style.type = 'text/css';
		    if (style.styleSheet){
		    	style.styleSheet.cssText = cloudStyle;	
		    }else{
		    	style.appendChild(document.createTextNode(cloudStyle));
		    }
			
		};
		evt.loadHtml = function(type){
			var title = "Cloud PKI Certificate Manager";
			self.wrap = htmlElements.generate(title, evt.type);
			var $wrap = self.wrap;
			return $wrap;			
		};
		evt.generateUI = function(){
			$("body").append(evt.loadHtml);
			self.isGenerate = true;
		};
		evt.show = function(){
			if(self.wrap){
				self.wrap.show();
			}
		};
		evt.drawContent = function(result){
			var table = self.wrap.find("#certificateTable tbody");
			if(result.count == 0){
				var dialog = new KicaCloudDialog();
				dialog.init("INFO", "There is no registered certificate.");
				return;
			}
			if(table.length == 0) {
				alert("Need refresh browser.");
				return;
			}
			var contentHtml = "";
			$.each(result.result, function(k,v){
				contentHtml += "<tr data-category='"+v.category_cd+"' data-secure-seq='"+v.seq+"' data-secure-dn='"+v.dn+"' data-revoked='"+v.revokeYn+"' onclick='KicaCloudUIEvent.certificateSelect(this, \""+evt.type+"\")'>";
				contentHtml += "<td>";
				contentHtml += v.name;
				contentHtml += "</td>";
				contentHtml += "<td>";
				contentHtml += v.category_nm ? v.category_nm : "";
				contentHtml += "</td>";
				contentHtml += "<td>";
				if(v.revokeYn == "Y"){
					contentHtml += "Revoked";
				}else{
					var expireDate = v.expire ? new Date(v.expire) : null;
					contentHtml += expireDate ? self.formatDate(expireDate) : "";	
				}
				contentHtml += "</td>";
				contentHtml += "<td>";
				contentHtml += v.authority;
				contentHtml += "</td>";
				contentHtml += "</tr>";
			});
			table.html(contentHtml);
			self.wrap.find("#cloudui-cancel-btn").on("click", function(){
				evt.destroy();
			})
			self.wrap.show();
		}
		evt.destroy = function(){
			if(self.wrap){
				self.wrap.remove();
			}
			self.isGenerate = false;
		};
		if($("#cloudStyleSheet").length == 0 ){
			evt.loadCss();	
		}
		evt.generateUI();
		return evt;
	}
	return self;
	
}