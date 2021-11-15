 package sample.java;


import java.io.File;
import java.io.FileInputStream;

import com.kica.eds.SignException;
import com.kica.eds.SignManager;
import com.kica.eds.SigningData;
import com.kica.eds.SigningResult;

public class LocalSignExample {
    public static void main(String[] args) {
        byte[] certBuffer = null; // 인증서를 저장할 버퍼 
        String password = "signgate1!"; // 인증서 비밀번호
        
        try {        	
        	// 파일 경로로 인증서 파일 읽어오기 
            File cert = new File("/app/product/web_data/eds/server.pfx");
            FileInputStream certStream = new FileInputStream(cert);
            int certByteLength = (int)cert.length();
            certBuffer = new byte[certByteLength];
            certStream.read(certBuffer, 0, certByteLength);
            certStream.close();
        }        
        // 인증서 파일이 없는 경우 예외 처리
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("import Cert error");
            return;
        }
                 
        try{ 
        	// 서명 매니저 객체 생성
            SignManager m = new SignManager();
            m.setPropertyPath("C:/Users/khan/Central/Workspace/2020/003 KICA EDS/WorspacesTemp/kica-eds-sample/build/classes/");
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
            signinfo.setPfxData(certBuffer); // 인증서 버퍼
            signinfo.setPassword(password); // 인증서 비밀번호
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
            signinfo.setContactInfo("signer@signgate.com"); // 서명 위젯에 표시될 서명자 연락처

            // 기본적으로 모두 true
            signinfo.setSignatureVisible(true); // 서명 위젯 표시 여부 
            signinfo.setSignDateVisible(true); // 서명 위젯에 서명 날짜 표시 여부
            signinfo.setReasnVisible(true); // 서명 위젯에 서명 사유 표시 여부
            signinfo.setSignerVisible(true); // 서명 위젯에 서명자 이름 표시 여부
            signinfo.setDocIDVisible(true); // 서명 위젯에 문서 식별 번호 표시 여부
            
            signinfo.setChainVaild(true); // 체인 검증 수행 여부

            // 전자서명 수행            
            SigningResult sresult = m.doLocalSigning(signinfo);
            
            // 서명 결과 출력
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
