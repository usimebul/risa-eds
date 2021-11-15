package sample.java;

import com.kica.eds.SignException;
import com.kica.eds.SignManager;
import com.kica.eds.SigningData;
import com.kica.eds.SigningResult;

public class TimeStampSignExample {

	public static void main(String[] args) {
		try {
			// 서명 매니저 객체 생성
			SignManager m = new SignManager();
			// JNI 라이브러리 로드
			m.lib_init();
			// 새로운 서명 필드를 생성하여 서명
			m.setEmptyFieldSign(false);
			// 기존 서명 필드에 서명
			// 이 경우에는 SigningData 객체에 signatureFieldName 값을 설정해주어야 함.
			// m.setEmptyFieldSign(true);

			SigningData signinfo = new SigningData();

			signinfo.setDocFilePath("C:/Users/bts/pkidemo_workspace/risa-eds/upload/sample.pdf"); // 서명할 PDF 파일 경로
			signinfo.setSignImgPath("C:/Users/bts/pkidemo_workspace/risa-eds/upload/sign.jpg"); // 서명 이미지 파일 경로

			// 서명 위젯 위치 설정
			signinfo.setWidth(159);
			signinfo.setHeight(70);
			signinfo.setOffsetX(0);
			signinfo.setOffsetY(0);
			signinfo.setPage(1);
			signinfo.setUnifiedDocID("09876"); // 서명이 진행 중 일때 해당 문서에 락을 걸기 위해 사용되는 값
			signinfo.setDocID("123456789123"); // 서명 업데이트 시 필요한 임시 파일 생성에 사용되는 값
			signinfo.setSignerID("123123"); // 서명자 식별 번호

			signinfo.setSignerName("Test Signer"); // 서명 위젯에 표시될 서명자 이름
			signinfo.setSignReason("I agreed with the document."); // 서명 위젯에 표시될 서명 사유
			signinfo.setLocationIP("111.111.111.111"); // 서명 위젯에 표시될 서명 위치
			signinfo.setContactInfo("signer@govca.rw"); // 서명 위젯에 표시될 서명자 연락처

			signinfo.setDocIDVisible(false); // 서명 위젯에 문서 식별 번호 표시 여부
			signinfo.setReasnVisible(false); // 서명 위젯에 서명 사유 표시 여부
			signinfo.setSignerVisible(false); // 서명 위젯에 서명자 이름 표시 여부
			
			signinfo.setSignDateVisible(true); // 서명 위젯에 서명 날짜 표시 여부
			signinfo.setSignDateX(22); // 서명 위젯에서 서명 날짜 위치 x 좌표 
			signinfo.setSignDateY(20); // 서명 위젯에서 서명 날짜 위치 y 좌표
			
			signinfo.setSignTimeVisible(true); // 서명 위젯에서 서명 시간 표시 여부
			signinfo.setSignTimeX(25); // 서명 위젯에서 서명 시간 위치 x 좌표
			signinfo.setSignTimeY(15); // 서명 위젯에서 서명 시간 위치 y 좌표
			
			signinfo.setFontSize(4); // 서명 위젯 폰트 사이즈

			// 타임스탬프 수행 
			// 두 번째 파라미터를 true로 지정할 경우 해당 문서는 더 이상 수정 및 서명 불가 
			SigningResult sresult = m.doTimeStampSign(signinfo, false);

			System.out.println("docID              = " + sresult.getDocID());
			System.out.println("signingTime        = " + sresult.getSigningTime());
			System.out.println("Source             = " + sresult.getSource());
			System.out.println("padesLvl           = " + sresult.getPadesLvl());
			System.out.println("userDN             = " + sresult.getUserDN());
			System.out.println("subjectAltName     = " + sresult.getSubjectAltName());
			System.out.println("startDate          = " + sresult.getStartDate());
			System.out.println("endDate            = " + sresult.getEndDate());
			System.out.println("signerID           = " + sresult.getSignerID());
			System.out.println("signatureFieldName = " + sresult.getSignatureFieldName());
			System.out.println("page               = " + sresult.getPage());


		} catch (SignException e) {
			System.out.println(e.getMessage());
			System.out.println(e.getLogMessage());
		}
	}
}
