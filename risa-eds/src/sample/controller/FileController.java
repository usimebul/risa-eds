package sample.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.kica.eds.utils.SignUtils;

@Controller
public class FileController {

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody HashMap<String, Object> handleFileUpload(MultipartHttpServletRequest request) {
		HashMap<String, Object> responseVO = new HashMap<String, Object>();

		try {
			Iterator<String> itr = request.getFileNames();
			while (itr.hasNext()) {
				String uploadedFile = itr.next();
				MultipartFile file = request.getFile(uploadedFile);

				String encodedFilename = file.getOriginalFilename();
				String filename = new String(encodedFilename.getBytes("ISO-8859-1"), "UTF-8");

				byte[] bytes = file.getBytes();

				File dir = new File(SignUtils.getDocumentPath() + "/upload");
				if (!dir.exists())
					dir.mkdirs();

				File serverFile = new File(SignUtils.getDocumentPath() + "/upload/" + filename);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				responseVO.put("result", "uploaded");
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseVO.put("result", "upload failed");
		}
		return responseVO;
	}

	@RequestMapping(value = "/download/{fileName:.+}", method = RequestMethod.GET)
	public @ResponseBody FileSystemResource handleFileDownload(@PathVariable String fileName) {
		try {
			File file = new File(SignUtils.getDocumentPath() + "/upload/"  + fileName);
			return new FileSystemResource(file);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
