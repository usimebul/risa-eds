package sample.java;

import com.kica.eds.SignException;
import com.kica.eds.SignManager;
import com.kica.eds.SigningData;
import com.kica.eds.SigningResult;
import com.kica.eds.sign.PKCS7Detached;


public class RemoteSignExample {

    public static void main(String[] args) {
        try{
        	// 서명 매니저 객체 생성
        	SignManager m = new SignManager();
        	// JNI 라이브러리 로드
            m.lib_init();
            // 새로운 서명 필드를 생성하여 서명
            m.setEmptyFieldSign(false);
            // 기존 서명 필드에 서명
            // 이 경우에는 SigningData 객체에 signatureFieldName 값을 설정해주어야 함.
            // m.setEmptyFieldSign(true);
        	// 서명 데이터 객체 생성
            SigningData signinfo  = new SigningData();            

            
            signinfo.setDocFilePath("C:/Users/bts/pkidemo_workspace/risa-eds/upload/sample.pdf"); // 서명할 PDF 파일 경로
            signinfo.setSignImgPath("C:/Users/bts/pkidemo_workspace/risa-eds/upload/sign.jpg"); // 서명 이미지 파일 경로       
            
            signinfo.setUnifiedDocID("09876"); // 서명이 진행 중 일때 해당 문서에 락을 걸기 위해 사용되는 값
            signinfo.setDocID("123456789123"); // 서명 업데이트 시 필요한 임시 파일 생성에 사용되는 값 
            signinfo.setSignerID("123123"); // 서명자 식별 번호
            
            signinfo.setSignerName("Test Signer"); // 서명 위젯에 표시될 서명자 이름 
            signinfo.setSignReason("I agreed with the document."); // 서명 위젯에 표시될 서명 사유
            signinfo.setLocationIP("111.111.111.111"); // 서명 위젯에 표시될 서명 위치
            signinfo.setContactInfo("signer@signgate.com"); // 서명 위젯에 표시될 서명자 연락처

            // 기본적으로 모두 true
            signinfo.setSignatureVisible(true); // 서명 위젯 표시 여부 
            signinfo.setSignDateVisible(true); // 서명 위젯에 서명 날짜 표시 여부
            signinfo.setReasnVisible(true); // 서명 위젯에 서명 사유 표시 여부
            signinfo.setSignerVisible(true); // 서명 위젯에 서명자 이름 표시 여부
            signinfo.setDocIDVisible(true); // 서명 위젯에 문서 식별 번호 표시 여부
            
            signinfo.setChainVaild(true); // 체인 검증 수행 여부


            // Remote Signing is done by 3 steps
            // 1. Make a temporary PDF document and sign without signature data to get the hash value of the PDF document.
            // 2. Generate signature data with the hash value.
            // 3. Update the PDF document with the generated signature.

            // Do the first step.
            SigningResult sresult = m.doRemoteSign(signinfo);

            System.out.println("docID              = " + sresult.getDocID()              );
            System.out.println("signingTime        = " + sresult.getSigningTime()        );
            System.out.println("Source             = " + sresult.getSource()             ); // Hash Value of PDF Document.
            System.out.println("padesLvl           = " + sresult.getPadesLvl()           );
            System.out.println("userDN             = " + sresult.getUserDN()             );
            System.out.println("signerID           = " + sresult.getSignerID()           );
            System.out.println("signatureFieldName = " + sresult.getSignatureFieldName() );
            System.out.println("page               = " + sresult.getPage()               );


            // Do the second step.            
            PKCS7Detached test = new PKCS7Detached(); // The function is only for demonstration.
            String certPath = "w:/signCert.der";
            String keyPath = "w:/signPri.key";
            String passwd = "signgate1!";

    		String[] pkcs7 = test.p7Msg(sresult.getSource().getBytes(), certPath, keyPath, passwd);

    		System.out.println("pkcs7[1]  " + pkcs7[1]);
    		System.out.println("pkcs7[0]  " + pkcs7[0]);
    		
    		// Do the third step.
    		signinfo  = new SigningData();    		
            signinfo.setDocFilePath("C:/Users/bts/pkidemo_workspace/risa-eds/upload/result.pdf"); // In this case, the docFilePath is new file name.
            signinfo.setSignatureFieldName(sresult.getSignatureFieldName());
            signinfo.setUnifiedDocID("09876");
            signinfo.setDocID("123456789123"); // 서명 업데이트 시 필요한 임시 파일 생성에 사용되는 값 
            signinfo.setSignerID("123123"); // 서명자 식별 번호
    		signinfo.setP7Message(pkcs7[1]);
    		signinfo.setCertData(pkcs7[0]);
    		signinfo.setChainVaild(true);

    		// Update the PDF document with the generated signature data.
    		sresult = m.doRemoteSignUpdate(signinfo);

            System.out.println("docID              = " + sresult.getDocID()              );
            System.out.println("signingTime        = " + sresult.getSigningTime()        );
            System.out.println("Source             = " + sresult.getSource()             );
            System.out.println("padesLvl           = " + sresult.getPadesLvl()           );
            System.out.println("userDN             = " + sresult.getUserDN()             );
            System.out.println("subjectAltName     = " + sresult.getSubjectAltName()     );
            System.out.println("startDate          = " + sresult.getStartDate()          );
            System.out.println("endDate            = " + sresult.getEndDate()            );
            System.out.println("signerID           = " + sresult.getSignerID()           );
            System.out.println("signatureFieldName = " + sresult.getSignatureFieldName() );
            System.out.println("page               = " + sresult.getPage()               );

        }catch(SignException e){        	
        	System.out.println(e.getMessage());        	
        	System.out.println(e.getLogMessage());
        }
    }
}
