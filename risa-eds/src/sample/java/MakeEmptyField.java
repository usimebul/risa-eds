package sample.java;

import com.kica.eds.SignException;
import com.kica.eds.SignManager;
import com.kica.eds.SigningData;
import com.kica.eds.SigningResult;


public class MakeEmptyField {
    public static void main(String[] args) {
        try{
        	// 서명 매니저 객체 생성
            SignManager m = new SignManager();
            // JNI 라이브러리 로드
            m.lib_init(); 

            SigningData signinfo  = new SigningData(); // 서명 데이터 객체 생성
            signinfo.setDocFilePath("C:/Users/bts/pkidemo_workspace/risa-eds/upload/sample.pdf"); // 서명할 PDF 파일 경로

            signinfo.setSignatureFieldName("signature_0"); // 생성할 서명 필드 이름 설정 (중복되지 않아야 함)
            signinfo.setUnifiedDocID("12382"); // 서명이 진행 중 일때 해당 문서에 락을 걸기 위해 사용되는 값
            signinfo.setSignerID("!234"); // 서명자 식별번호
            
            // 서명 위젯 위치 설정
            signinfo.setWidth(150);
            signinfo.setHeight(50);
            signinfo.setOffsetX(310);
            signinfo.setOffsetY(250);
            signinfo.setPage(1);

            // 서명 필드 생성
            SigningResult sresult = m.doMakeEmptyField(signinfo);

            // 서명 필드 생성 결과 출력
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
