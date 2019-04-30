package com.app.runners.model;

import com.app.runners.rest.RestConstants;

/**
 * Created by sergiocirasa on 20/8/17.
 */

public class Documentation {
    public String swornStatement;
    public String medicalCertificate;
    public String swornStatementExpirationDate;
    public String medicalCertificateExpirationDate;

    public String getSwornStatementPath(){
        if(swornStatement!=null)
            return RestConstants.IMAGE_HOST + swornStatement;
        else return null;
    }

    public String getMedicalCertificatePath(){
        if(medicalCertificate!=null)
            return RestConstants.IMAGE_HOST + medicalCertificate;
        else return null;
    }
}
