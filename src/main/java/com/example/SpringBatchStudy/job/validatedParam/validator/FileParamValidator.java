package com.example.SpringBatchStudy.job.validatedParam.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

/**
 * validation을 따로 뺴서, 재사용성을 강화한다.
 */
public class FileParamValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException{
        String fileName = parameters.getString("fileName");

        if(!StringUtils.endsWithIgnoreCase(fileName, "csv")){
            throw new JobParametersInvalidException("This is not csv file");
        }
    }
}
