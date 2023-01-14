package com.howdev.app.api;

import com.howdev.api.model.BaseRequest;
import com.howdev.api.model.BaseResponse;
import com.howdev.api.model.DecisionRequest;
import com.howdev.api.model.DecisionResponse;
import com.howdev.api.provider.DecisionApi;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * DecisionApiImpl class
 *
 * @author haozhifeng
 * @date 2023/01/14
 */
@Service
public class DecisionApiImpl implements DecisionApi {
    @Override
    public BaseResponse<DecisionResponse> syncDecision(@NotNull(message = "decisionRequest is null") @Valid BaseRequest<DecisionRequest> decisionRequest) {
        return null;
    }

    @Override
    public BaseResponse<DecisionResponse> asyncDecision(@NotNull(message = "decisionRequest is null") @Valid BaseRequest<DecisionRequest> decisionRequest) {
        return null;
    }
}
