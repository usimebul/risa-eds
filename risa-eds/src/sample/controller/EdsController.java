package sample.controller;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.kica.eds.SignException;
import com.kica.eds.SignManager;
import com.kica.eds.SigningData;
import com.kica.eds.SigningResult;
import com.kica.eds.utils.SignUtils;

@Controller
public class EdsController {
	private static final Logger LOGGER = LoggerFactory.getLogger(EdsController.class);

	private static SignManager signManager;

	public EdsController() throws SignException {
		signManager = new SignManager();
		// File location for eds.properties and trustedcertificate.pem
		// Basically, search in the classpath:/
		signManager.setPropertyPath("D:\\workspace-eds\\risa-eds\\src\\");
		// Load JNI Library
		signManager.lib_init();
	}

	// Get PDF file hash
	@RequestMapping(value = "/getPDFHash", method = RequestMethod.POST, consumes = "text/plain")
	public @ResponseBody HashMap<String, Object> getPDFHash(HttpServletRequest request, @RequestBody String body)
			throws SignException {

		// Response Object
		HashMap<String, Object> responseVO = new HashMap<String, Object>();
		try {

			// Convert the Request Body to JSON object
			JSONObject signInfo = new JSONObject(body);
			SigningData signingData = new SigningData();

			signingData.setDocFilePath(SignUtils.getDocumentPath() + "/upload/"+ signInfo.getString("pdfFileName")); // original PDF file path
			signingData.setSignImgPath(SignUtils.getDocumentPath() + "/upload/"+ signInfo.getString("signImgName")); // signer sign image file path

			signingData.setUnifiedDocID(signInfo.getString("unifiedDocID")); // a value for locking while a certain PDF
																				// is singing
			signingData.setDocID(signInfo.getString("docID")); // a value to make temporary file while a certain PDF is
																// singing
			signingData.setSignerID(signInfo.getString("signerID")); // signer unique number

			signingData.setSignerName(signInfo.getString("signerName")); // signer name
			signingData.setSignReason(signInfo.getString("reason")); // reason
			signingData.setLocationIP(signInfo.getString("locationIP")); // location
			signingData.setContactInfo(signInfo.getString("signerEmail")); // email

			// in case making a signature to the already signed signature filed
			if (signInfo.has("signatureFieldName") && !signInfo.isNull("signatureFieldName")) {
				signManager.setEmptyFieldSign(true);
				// set signature filed name
				signingData.setSignatureFieldName(signInfo.getString("signatureFieldName"));
			}
			// in case making a signature to new signature filed
			else {
				signManager.setEmptyFieldSign(false);
				// set the location of signature widget
				signingData.setWidth(signInfo.getInt("width"));
				signingData.setHeight(signInfo.getInt("height"));
				signingData.setOffsetX(signInfo.getInt("offsetX"));
				signingData.setOffsetY(signInfo.getInt("offsetY"));
				signingData.setPage(signInfo.getInt("page"));
			}

			// widget display or not;
			if (signInfo.has("signatureVisible") && !signInfo.isNull("signatureVisible")) {
				signingData.setSignatureVisible(signInfo.getBoolean("signatureVisible"));
			}

			// signing date display or not
			if (signInfo.has("signDateVisible") && !signInfo.isNull("signDateVisible")) {
				signingData.setSignDateVisible(signInfo.getBoolean("signDateVisible"));
			}

			// reason display or not
			if (signInfo.has("reasnVisible") && !signInfo.isNull("reasnVisible")) {
				signingData.setReasnVisible(signInfo.getBoolean("reasnVisible"));
			}

			// singer name display or not
			if (signInfo.has("signerVisible") && !signInfo.isNull("signerVisible")) {
				signingData.setSignerVisible(signInfo.getBoolean("signerVisible"));
			}

			// signer unique number display or not
			if (signInfo.has("docIDVisible") && !signInfo.isNull("docIDVisible")) {
				signingData.setDocIDVisible(signInfo.getBoolean("docIDVisible"));
			}

			// get PDF hash
			SigningResult signingResult = signManager.doRemoteSign(signingData);

			// Logger
			LOGGER.info("docID				:" + signingResult.getDocID());
			LOGGER.info("signingTime		:" + signingResult.getSigningTime());
			LOGGER.info("hash				:" + signingResult.getSource());
			LOGGER.info("padesLv1			:" + signingResult.getPadesLvl());
			LOGGER.info("userDN				:" + signingResult.getUserDN());
			LOGGER.info("signerID			:" + signingResult.getSignerID());
			LOGGER.info("signautreFieldName	:" + signingResult.getSignatureFieldName());
			LOGGER.info("page				:" + signingResult.getPage());

			// set response body
			responseVO.put("signautreFieldName", signingResult.getSignatureFieldName());
			responseVO.put("hash", signingResult.getSource());
			responseVO.put("docID", signingResult.getDocID());
			responseVO.put("signerID", signingResult.getSignerID());

		} catch (SignException e) {
			System.out.println("ERROR CODE: " + e.getErrorCode());
			System.out.println("ERROR METHOD: " + e.getErrorMethodName());
			System.out.println("ERROR LINE NUMBER: " + e.getErrorLineNumber());
			System.out.println("ERROR MESSAGE: " + e.getMessage());
			responseVO.put("ERROR", e);
			e.printStackTrace();
		} catch (Exception e) {
			responseVO.put("ERROR", e);
			e.printStackTrace();
		}

		return responseVO;
	}

	// Get PDF file hash
	@RequestMapping(value = "/getPDFHash2", method = RequestMethod.POST, consumes = "text/plain")
	public @ResponseBody HashMap<String, Object> getPDFHash2(HttpServletRequest request, @RequestBody String body)
			throws SignException {
		// Response Object
		HashMap<String, Object> responseVO = new HashMap<String, Object>();
		try {

			// Convert the Request Body to JSON object
			JSONObject signInfo = new JSONObject(body);
			SigningData signingData = new SigningData();

			signingData.setUnifiedDocID("09876"); // Todo: set default value
			signingData.setDocID("123456789123"); // Todo: set default value
			signingData.setSignerID("944"); // Todo: set default value
			signingData.setSignerName(signInfo.getString("signerName"));
			signingData.setSignReason("RISA Digital Documnet Signing"); // Todo: set default value

			signingData.setDocFilePath(SignUtils.getDocumentPath() + "/upload/" + signInfo.getString("pdfFileName"));
			signingData.setSignImgPath(SignUtils.getDocumentPath() + "/upload/" + signInfo.getString("signImgPath"));
			signingData.setWidth(signInfo.getInt("width"));
			signingData.setHeight(signInfo.getInt("height"));
			signingData.setOffsetX(signInfo.getInt("offsetX"));
			signingData.setOffsetY(signInfo.getInt("offsetY"));
			signingData.setPage(signInfo.getInt("page"));

			signManager.setEmptyFieldSign(false);

			signingData.setReasnVisible(false);
			signingData.setSignerVisible(false);
			signingData.setDocIDVisible(false);

			// get PDF hash
			SigningResult signingResult = signManager.doRemoteSign(signingData);

			// Logger
			LOGGER.info("docID				:" + signingResult.getDocID());
			LOGGER.info("signingTime		:" + signingResult.getSigningTime());
			LOGGER.info("hash				:" + signingResult.getSource());
			LOGGER.info("padesLv1			:" + signingResult.getPadesLvl());
			LOGGER.info("userDN				:" + signingResult.getUserDN());
			LOGGER.info("signerID			:" + signingResult.getSignerID());
			LOGGER.info("signautreFieldName	:" + signingResult.getSignatureFieldName());
			LOGGER.info("page				:" + signingResult.getPage());

			// set response body
			responseVO.put("signautreFieldName", signingResult.getSignatureFieldName());
			responseVO.put("hash", signingResult.getSource());
		} catch (SignException e) {
			System.out.println("ERROR CODE: " + e.getErrorCode());
			System.out.println("ERROR METHOD: " + e.getErrorMethodName());
			System.out.println("ERROR LINE NUMBER: " + e.getErrorLineNumber());
			System.out.println("ERROR MESSAGE: " + e.getMessage());
			responseVO.put("ERROR", e);
			e.printStackTrace();
		} catch (Exception e) {
			responseVO.put("ERROR", e);
			e.printStackTrace();
		}

		return responseVO;
	}

	@RequestMapping(value = "/addDetachedSignature", method = RequestMethod.POST)
	public @ResponseBody HashMap<String, Object> addDetachedSignature(HttpServletRequest request,
			@RequestBody String body) throws SignException {
		HashMap<String, Object> responseVO = new HashMap<String, Object>();
		try {
			JSONObject signInfo = new JSONObject(body);
			SigningData signingData = new SigningData();
			
			signingData.setDocFilePath(SignUtils.getDocumentPath() + "/upload/"+ signInfo.getString("pdfFileName"));
			signingData.setSignatureFieldName(signInfo.getString("signatureFieldName"));
			signingData.setUnifiedDocID(signInfo.getString("unifiedDocID"));
			signingData.setDocID(signInfo.getString("docID"));
			signingData.setSignerID(signInfo.getString("signerID"));
			signingData.setP7Message(signInfo.getString("p7Message"));
			signingData.setCertData(signInfo.getString("certData"));

			System.out.println( "111: " + SignUtils.getDocumentPath() + "/upload/"+ signInfo.getString("pdfFileName") );
			System.out.println( "222: " +   signInfo.getString("signatureFieldName") );
			
			
			// Chain validation
			if (signInfo.has("chainValid") && !signInfo.isNull("chainValid")) {
				signingData.setChainVaild(signInfo.getBoolean("chainValid"));
			}

			// update signature data
			SigningResult signingResult = signManager.doRemoteSignUpdate(signingData);

			// Logger
			LOGGER.info("docID				:" + signingResult.getDocID());
			LOGGER.info("signingTime		:" + signingResult.getSigningTime());
			LOGGER.info("hash				:" + signingResult.getSource());
			LOGGER.info("padesLv1			:" + signingResult.getPadesLvl());
			LOGGER.info("userDN				:" + signingResult.getUserDN());
			LOGGER.info("subjectAltName 	:" + signingResult.getSubjectAltName());
			LOGGER.info("signerID			:" + signingResult.getSignerID());
			LOGGER.info("startDate			:" + signingResult.getStartDate());
			LOGGER.info("endDate			:" + signingResult.getEndDate());
			LOGGER.info("signautreFieldName	:" + signingResult.getSignatureFieldName());
			LOGGER.info("page				:" + signingResult.getPage());

			responseVO.put("result", "Success");
		} catch (Exception e) {
			System.out.println("ERROR MESSAGE: " + e.getMessage());
			responseVO.put("ERROR", e);
			e.printStackTrace();
		}

		return responseVO;
	}

	@RequestMapping(value = "/addDetachedSignature2", method = RequestMethod.POST)
	public @ResponseBody HashMap<String, Object> addDetachedSignature2(HttpServletRequest request,
			@RequestBody String body) throws SignException {
		HashMap<String, Object> responseVO = new HashMap<String, Object>();
		try {
			JSONObject signInfo = new JSONObject(body);
			SigningData signingData = new SigningData();

			signingData.setDocFilePath(SignUtils.getDocumentPath() + File.separator + signInfo.getString("pdfFileName"));
			signingData.setSignatureFieldName(signInfo.getString("signatureFieldName"));
			signingData.setP7Message(signInfo.getString("p7Message"));
			signingData.setCertData(signInfo.getString("certData"));
			signingData.setUnifiedDocID("09876"); // Todo: set default value
			signingData.setDocID("123456789123"); // Todo: set default value
			signingData.setSignerID("944"); // Todo: set default value

			// update signature data
			SigningResult signingResult = signManager.doRemoteSignUpdate(signingData);

			// Logger
			LOGGER.info("docID				:" + signingResult.getDocID());
			LOGGER.info("signingTime		:" + signingResult.getSigningTime());
			LOGGER.info("hash				:" + signingResult.getSource());
			LOGGER.info("padesLv1			:" + signingResult.getPadesLvl());
			LOGGER.info("userDN				:" + signingResult.getUserDN());
			LOGGER.info("subjectAltName 	:" + signingResult.getSubjectAltName());
			LOGGER.info("signerID			:" + signingResult.getSignerID());
			LOGGER.info("startDate			:" + signingResult.getStartDate());
			LOGGER.info("endDate			:" + signingResult.getEndDate());
			LOGGER.info("signautreFieldName	:" + signingResult.getSignatureFieldName());
			LOGGER.info("page				:" + signingResult.getPage());

			responseVO.put("result", "Success");
		} catch (Exception e) {
			System.out.println("ERROR MESSAGE: " + e.getMessage());
			responseVO.put("ERROR", e);
			e.printStackTrace();
		}

		return responseVO;
	}

	@RequestMapping(value = "/sign", method = RequestMethod.POST)
	public @ResponseBody HashMap<String, Object> sign(HttpServletRequest request, @RequestBody String body)
			throws SignException {
		HashMap<String, Object> responseVO = new HashMap<String, Object>();
		try {
			JSONObject signInfo = new JSONObject(body);
			SigningData signingData = new SigningData();

			// read pfx certificate
			byte[] certBuffer = null;
			try {
				// DOC_DIR value in the eds.properties + /server.pfx
				
				File cert = new File(SignUtils.getDocumentPath() + "/servercert/server.pfx");
				FileInputStream certStream = new FileInputStream(cert);
				int certByteLength = (int) cert.length();
				certBuffer = new byte[certByteLength];
				certStream.read(certBuffer, 0, certByteLength);
				certStream.close();

			} catch (Exception e) {
				e.printStackTrace();
				responseVO.put("ERROR", e);
				return responseVO;
			}

			signingData.setDocFilePath(SignUtils.getDocumentPath() + "/upload/"+ signInfo.getString("pdfFileName"));
			signingData.setSignImgPath(SignUtils.getDocumentPath() + "/upload/"+ signInfo.getString("signImgName"));
			signingData.setPfxData(certBuffer); // certificate
			signingData.setPassword("signgate1!"); // certificate password

			System.out.println(SignUtils.getDocumentPath() + "/upload/"+ signInfo.getString("pdfFileName"));
			System.out.println(SignUtils.getDocumentPath() + "/upload/"+ signInfo.getString("signImgName"));
			
signingData.setUnifiedDocID(signInfo.getString("unifiedDocID"));
			signingData.setDocID(signInfo.getString("docID"));
			signingData.setSignerID(signInfo.getString("signerID"));

			signingData.setSignerName(signInfo.getString("signerName"));
			signingData.setSignReason(signInfo.getString("reason"));
			signingData.setLocationIP(signInfo.getString("locationIP"));
			signingData.setContactInfo(signInfo.getString("signerEmail"));

			// in case making a signature to the already signed signature filed
			if (signInfo.has("signatureFieldName") && !signInfo.isNull("signatureFieldName")) {
				signManager.setEmptyFieldSign(true);

				signingData.setSignatureFieldName(signInfo.getString("signatureFieldName"));
			}
			// in case making a signature to new signature filed
			else {
				signManager.setEmptyFieldSign(false);
				// signing widget
				signingData.setWidth(signInfo.getInt("width"));
				signingData.setHeight(signInfo.getInt("height"));
				signingData.setOffsetX(signInfo.getInt("offsetX"));
				signingData.setOffsetY(signInfo.getInt("offsetY"));
				signingData.setPage(signInfo.getInt("page"));
			}

			// widget display or not;
			if (signInfo.has("signatureVisible") && !signInfo.isNull("signatureVisible")) {
				signingData.setSignatureVisible(signInfo.getBoolean("signatureVisible"));
			}

			// signing date display or not
			if (signInfo.has("signDateVisible") && !signInfo.isNull("signDateVisible")) {
				signingData.setSignDateVisible(signInfo.getBoolean("signDateVisible"));
			}

			// reason display or not
			if (signInfo.has("reasnVisible") && !signInfo.isNull("reasnVisible")) {
				signingData.setReasnVisible(signInfo.getBoolean("reasnVisible"));
			}

			// singer name display or not
			if (signInfo.has("signerVisible") && !signInfo.isNull("signerVisible")) {
				signingData.setSignerVisible(signInfo.getBoolean("signerVisible"));
			}

			// signer unique number display or not
			if (signInfo.has("docIDVisible") && !signInfo.isNull("docIDVisible")) {
				signingData.setDocIDVisible(signInfo.getBoolean("docIDVisible"));
			}

			// Chain validation or not
			if (signInfo.has("chainValid") && !signInfo.isNull("chainValid")) {
				signingData.setChainVaild(signInfo.getBoolean("chainValid"));
			}

			// do sign on the server
			SigningResult signingResult = signManager.doLocalSigning(signingData);

			// Logger
			LOGGER.info("docID				:" + signingResult.getDocID());
			LOGGER.info("signingTime		:" + signingResult.getSigningTime());
			LOGGER.info("hash				:" + signingResult.getSource());
			LOGGER.info("padesLv1			:" + signingResult.getPadesLvl());
			LOGGER.info("userDN				:" + signingResult.getUserDN());
			LOGGER.info("subjectAltName 	:" + signingResult.getSubjectAltName());
			LOGGER.info("signerID			:" + signingResult.getSignerID());
			LOGGER.info("startDate			:" + signingResult.getStartDate());
			LOGGER.info("endDate			:" + signingResult.getEndDate());
			LOGGER.info("signautreFieldName	:" + signingResult.getSignatureFieldName());
			LOGGER.info("page				:" + signingResult.getPage());

			// set response data
			responseVO.put("result", "Success");
		} catch (Exception e) {
			System.out.println("ERROR MESSAGE: " + e.getMessage());
			responseVO.put("ERROR", e);
			e.printStackTrace();
		}

		return responseVO;
	}

	@RequestMapping(value = "/signByThree", method = RequestMethod.POST)
	public @ResponseBody HashMap<String, Object> signByThree(HttpServletRequest request, @RequestBody String body)
			throws SignException {
		HashMap<String, Object> responseVO = new HashMap<String, Object>();
		try {
			JSONObject signInfo = new JSONObject(body);
			SigningData signingData = new SigningData();

			signingData.setDocFilePath(SignUtils.getDocumentPath() + "/upload/"+ signInfo.getString("pdfFileName"));
			signingData.setSignImgPath(SignUtils.getDocumentPath() + "/upload/"+ signInfo.getString("signImgName"));
			signingData.setUnifiedDocID(signInfo.getString("unifiedDocID"));
			signingData.setDocID(signInfo.getString("docID"));
			signingData.setSignerID(signInfo.getString("signerID"));

			signingData.setSignerName(signInfo.getString("signerName"));
			signingData.setSignReason(signInfo.getString("reason"));
			signingData.setLocationIP(signInfo.getString("locationIP"));
			signingData.setContactInfo(signInfo.getString("signerEmail"));

			// 서명 위젯 위치 설정
			signingData.setWidth(signInfo.getInt("width"));
			signingData.setHeight(signInfo.getInt("height"));
			signingData.setOffsetX(signInfo.getInt("offsetX"));
			signingData.setOffsetY(signInfo.getInt("offsetY"));
			signingData.setPage(signInfo.getInt("page"));

			// widget display or not;
			if (signInfo.has("signatureVisible") && !signInfo.isNull("signatureVisible")) {
				signingData.setSignatureVisible(signInfo.getBoolean("signatureVisible"));
			}

			// signing date display or not
			if (signInfo.has("signDateVisible") && !signInfo.isNull("signDateVisible")) {
				signingData.setSignDateVisible(signInfo.getBoolean("signDateVisible"));
			}

			// reason display or not
			if (signInfo.has("reasnVisible") && !signInfo.isNull("reasnVisible")) {
				signingData.setReasnVisible(signInfo.getBoolean("reasnVisible"));
			}

			// singer name display or not
			if (signInfo.has("signerVisible") && !signInfo.isNull("signerVisible")) {
				signingData.setSignerVisible(signInfo.getBoolean("signerVisible"));
			}

			// signer unique number display or not
			if (signInfo.has("docIDVisible") && !signInfo.isNull("docIDVisible")) {
				signingData.setDocIDVisible(signInfo.getBoolean("docIDVisible"));
			}

			// Chain validation or not
			if (signInfo.has("chainValid") && !signInfo.isNull("chainValid")) {
				signingData.setChainVaild(signInfo.getBoolean("chainValid"));
			}

			// read pfx certificate
			byte[] certBuffer = null;
			try {
				// DOC_DIR value in the eds.properties + /server.pfx
				File cert = new File(SignUtils.getDocumentPath() + "/servercert/server.pfx");
				FileInputStream certStream = new FileInputStream(cert);
				int certByteLength = (int) cert.length();
				certBuffer = new byte[certByteLength];
				certStream.read(certBuffer, 0, certByteLength);
				certStream.close();

			} catch (Exception e) {
				e.printStackTrace();
				responseVO.put("ERROR", e);
				return responseVO;
			}

			signingData.setPfxData(certBuffer); // certificate
			signingData.setPassword("signgate1!"); // certificate password
			// in case making a signature to new signature filed
			signManager.setEmptyFieldSign(false);

			// do sign on the server
			SigningResult signingResult = signManager.doLocalSigning(signingData);

			// Logger
			LOGGER.info("docID				:" + signingResult.getDocID());
			LOGGER.info("signingTime		:" + signingResult.getSigningTime());
			LOGGER.info("hash				:" + signingResult.getSource());
			LOGGER.info("padesLv1			:" + signingResult.getPadesLvl());
			LOGGER.info("userDN				:" + signingResult.getUserDN());
			LOGGER.info("subjectAltName 	:" + signingResult.getSubjectAltName());
			LOGGER.info("signerID			:" + signingResult.getSignerID());
			LOGGER.info("startDate			:" + signingResult.getStartDate());
			LOGGER.info("endDate			:" + signingResult.getEndDate());
			LOGGER.info("signautreFieldName	:" + signingResult.getSignatureFieldName());
			LOGGER.info("page				:" + signingResult.getPage());

			// read pfx certificate
			certBuffer = null;
			try {
				// DOC_DIR value in the eds.properties + /server.pfx
				File cert = new File(SignUtils.getDocumentPath() + "/servercert/server.pfx");
				FileInputStream certStream = new FileInputStream(cert);
				int certByteLength = (int) cert.length();
				certBuffer = new byte[certByteLength];
				certStream.read(certBuffer, 0, certByteLength);
				certStream.close();

			} catch (Exception e) {
				e.printStackTrace();
				responseVO.put("ERROR", e);
				return responseVO;
			}

			signingData.setPfxData(certBuffer); // certificate
			signingData.setPassword("signgate1!"); // certificate password
			signingData.setOffsetY(signInfo.getInt("offsetY") + 100);
			// in case making a signature to new signature filed
			signManager.setEmptyFieldSign(false);

			// do sign on the server
			signingResult = signManager.doLocalSigning(signingData);

			// Logger
			LOGGER.info("docID				:" + signingResult.getDocID());
			LOGGER.info("signingTime		:" + signingResult.getSigningTime());
			LOGGER.info("hash				:" + signingResult.getSource());
			LOGGER.info("padesLv1			:" + signingResult.getPadesLvl());
			LOGGER.info("userDN				:" + signingResult.getUserDN());
			LOGGER.info("subjectAltName 	:" + signingResult.getSubjectAltName());
			LOGGER.info("signerID			:" + signingResult.getSignerID());
			LOGGER.info("startDate			:" + signingResult.getStartDate());
			LOGGER.info("endDate			:" + signingResult.getEndDate());
			LOGGER.info("signautreFieldName	:" + signingResult.getSignatureFieldName());
			LOGGER.info("page				:" + signingResult.getPage());

			// read pfx certificate
			certBuffer = null;
			try {
				// DOC_DIR value in the eds.properties + /server.pfx
				
				File cert = new File(SignUtils.getDocumentPath() + "/servercert/server.pfx");
				//File cert = new File("C:/Users/bts/pkidemo_workspace/risa-eds/src/server.pfx");
				
				FileInputStream certStream = new FileInputStream(cert);
				int certByteLength = (int) cert.length();
				certBuffer = new byte[certByteLength];
				certStream.read(certBuffer, 0, certByteLength);
				certStream.close();

			} catch (Exception e) {
				e.printStackTrace();
				responseVO.put("ERROR", e);
				return responseVO;
			}

			signingData.setPfxData(certBuffer); // certificate
			signingData.setPassword("signgate1!"); // certificate password
			signingData.setOffsetY(signInfo.getInt("offsetY") + 200);

			// in case making a signature to new signature filed
			signManager.setEmptyFieldSign(false);

			// do sign on the server
			signingResult = signManager.doLocalSigning(signingData);

			// Logger
			LOGGER.info("docID				:" + signingResult.getDocID());
			LOGGER.info("signingTime		:" + signingResult.getSigningTime());
			LOGGER.info("hash				:" + signingResult.getSource());
			LOGGER.info("padesLv1			:" + signingResult.getPadesLvl());
			LOGGER.info("userDN				:" + signingResult.getUserDN());
			LOGGER.info("subjectAltName 	:" + signingResult.getSubjectAltName());
			LOGGER.info("signerID			:" + signingResult.getSignerID());
			LOGGER.info("startDate			:" + signingResult.getStartDate());
			LOGGER.info("endDate			:" + signingResult.getEndDate());
			LOGGER.info("signautreFieldName	:" + signingResult.getSignatureFieldName());
			LOGGER.info("page				:" + signingResult.getPage());

			// set response data
			responseVO.put("result", "Success");
		} catch (Exception e) {
			System.out.println("ERROR MESSAGE: " + e.getMessage());
			responseVO.put("ERROR", e);
			e.printStackTrace();
		}

		return responseVO;
	}

	// TimeStamping API
	@RequestMapping(value = "/timestamp", method = RequestMethod.POST)
	public @ResponseBody HashMap<String, Object> timestamp(HttpServletRequest request, @RequestBody String body)
			throws SignException {
		HashMap<String, Object> responseVO = new HashMap<String, Object>();
		try {
			// parse request body
			JSONObject signInfo = new JSONObject(body);
			SigningData signingData = new SigningData();

			signingData.setDocFilePath(SignUtils.getDocumentPath() + "/upload/"+ signInfo.getString("pdfFileName"));
			signingData.setSignImgPath(SignUtils.getDocumentPath() + "/upload/"+ signInfo.getString("signImgName"));

			signingData.setUnifiedDocID(signInfo.getString("unifiedDocID"));
			signingData.setDocID(signInfo.getString("docID"));
			signingData.setSignerID(signInfo.getString("signerID"));

			signingData.setSignerName(signInfo.getString("signerName"));
			signingData.setSignReason(signInfo.getString("reason"));
			signingData.setLocationIP(signInfo.getString("locationIP"));
			signingData.setContactInfo(signInfo.getString("signerEmail"));

			// in case making a signature to the already signed signature filed
			if (signInfo.has("signatureFieldName") && !signInfo.isNull("signatureFieldName")) {
				signManager.setEmptyFieldSign(true);
				//
				signingData.setSignatureFieldName(signInfo.getString("signatureFieldName"));
			}
			// in case making a signature to new signature filed
			else {
				signManager.setEmptyFieldSign(false);
				//
				signingData.setWidth(signInfo.getInt("width"));
				signingData.setHeight(signInfo.getInt("height"));
				signingData.setOffsetX(signInfo.getInt("offsetX"));
				signingData.setOffsetY(signInfo.getInt("offsetY"));
				signingData.setPage(signInfo.getInt("page"));
			}

			if (signInfo.has("signatureVisible") && !signInfo.isNull("signatureVisible")) {
				signingData.setSignatureVisible(signInfo.getBoolean("signatureVisible"));
			}

			if (signInfo.has("signDateVisible") && !signInfo.isNull("signDateVisible")) {
				signingData.setSignDateVisible(signInfo.getBoolean("signDateVisible"));
			}

			if (signInfo.has("reasnVisible") && !signInfo.isNull("reasnVisible")) {
				signingData.setReasnVisible(signInfo.getBoolean("reasnVisible"));
			}

			if (signInfo.has("signerVisible") && !signInfo.isNull("signerVisible")) {
				signingData.setSignerVisible(signInfo.getBoolean("signerVisible"));
			}

			if (signInfo.has("docIDVisible") && !signInfo.isNull("docIDVisible")) {
				signingData.setDocIDVisible(signInfo.getBoolean("docIDVisible"));
			}

			if (signInfo.has("chainValid") && !signInfo.isNull("chainValid")) {
				signingData.setChainVaild(signInfo.getBoolean("chainValid"));
			}

			// Do timestamp
			SigningResult signingResult = signManager.doTimeStampSign(signingData, false);

			// Logger
			LOGGER.info("docID				:" + signingResult.getDocID());
			LOGGER.info("signingTime		:" + signingResult.getSigningTime());
			LOGGER.info("hash				:" + signingResult.getSource());
			LOGGER.info("padesLv1			:" + signingResult.getPadesLvl());
			LOGGER.info("userDN				:" + signingResult.getUserDN());
			LOGGER.info("subjectAltName 	:" + signingResult.getSubjectAltName());
			LOGGER.info("signerID			:" + signingResult.getSignerID());
			LOGGER.info("startDate			:" + signingResult.getStartDate());
			LOGGER.info("endDate			:" + signingResult.getEndDate());
			LOGGER.info("signautreFieldName	:" + signingResult.getSignatureFieldName());
			LOGGER.info("page				:" + signingResult.getPage());

			responseVO.put("result", "Success");
		} catch (Exception e) {
			System.out.println("ERROR MESSAGE: " + e.getMessage());
			responseVO.put("ERROR", e);
			e.printStackTrace();
		}

		return responseVO;
	}

	@RequestMapping(value = "/makeEmptyField", method = RequestMethod.POST)
	public @ResponseBody HashMap<String, Object> makeEmptyField(HttpServletRequest request, @RequestBody String body)
			throws SignException {
		HashMap<String, Object> responseVO = new HashMap<String, Object>();
		try {
			JSONObject signInfo = new JSONObject(body);
			SigningData signingData = new SigningData();

			// absolute false
			signManager.setEmptyFieldSign(false);

			// Set Required SigningData attribute.
			signingData.setDocFilePath(SignUtils.getDocumentPath() + "/upload/"+ signInfo.getString("pdfFileName"));

			signingData.setUnifiedDocID(SignUtils.getDocumentPath() + "/upload/"+ signInfo.getString("unifiedDocID"));
			signingData.setSignerID(signInfo.getString("signerID"));

			// signature widget location
			signingData.setWidth(signInfo.getInt("width"));
			signingData.setHeight(signInfo.getInt("height"));
			signingData.setOffsetX(signInfo.getInt("offsetX"));
			signingData.setOffsetY(signInfo.getInt("offsetY"));
			signingData.setPage(signInfo.getInt("page"));

			// in case making a signature to the already signed signature filed
			if (signInfo.has("signatureFieldName") && !signInfo.isNull("signatureFieldName"))
				signingData.setSignatureFieldName(signInfo.getString("signatureFieldName"));

			// Do make empty signature field.
			SigningResult signingResult = signManager.doMakeEmptyField(signingData);

			LOGGER.info("docID				:" + signingResult.getDocID());
			LOGGER.info("signingTime		:" + signingResult.getSigningTime());
			LOGGER.info("hash				:" + signingResult.getSource());
			LOGGER.info("padesLv1			:" + signingResult.getPadesLvl());
			LOGGER.info("userDN				:" + signingResult.getUserDN());
			LOGGER.info("subjectAltName 	:" + signingResult.getSubjectAltName());
			LOGGER.info("signerID			:" + signingResult.getSignerID());
			LOGGER.info("startDate			:" + signingResult.getStartDate());
			LOGGER.info("endDate			:" + signingResult.getEndDate());
			LOGGER.info("signautreFieldName	:" + signingResult.getSignatureFieldName());
			LOGGER.info("page				:" + signingResult.getPage());

			responseVO.put("result", "Success");
			responseVO.put("signautreFieldName", signingResult.getSignatureFieldName());
		} catch (Exception e) {
			System.out.println("ERROR MESSAGE: " + e.getMessage());
			responseVO.put("ERROR", e);
			e.printStackTrace();
		}

		return responseVO;
	}
}
