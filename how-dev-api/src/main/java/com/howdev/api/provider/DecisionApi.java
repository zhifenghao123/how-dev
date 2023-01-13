package com.howdev.api.provider;

import com.howdev.api.model.BaseRequest;
import com.howdev.api.model.BaseResponse;
import com.howdev.api.model.DecisionRequest;
import com.howdev.api.model.DecisionResponse;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * DecisionApi class
 *
 * @author haozhifeng
 * @date 2023/01/13
 */
@Validated
public interface DecisionApi {
   /**
    * syncDecision
    * 
    * @param decisionRequest decisionRequest
    * @return: 
    * @author: haozhifeng     
    */
    BaseResponse<DecisionResponse> syncDecision(@NotNull(message = "decisionRequest is null") @Valid
                                                        BaseRequest<DecisionRequest> decisionRequest);


    /**
     * asyncDecision
     * 
     * @param decisionRequest decisionRequest
     * @return: 
     * @author: haozhifeng     
     */
    BaseResponse<DecisionResponse> asyncDecision(@NotNull(message = "decisionRequest is null") @Valid
                                                        BaseRequest<DecisionRequest> decisionRequest);
}
