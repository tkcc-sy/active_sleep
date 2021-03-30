package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paramount.bed.data.model.AdvertiseModel;
import com.paramount.bed.data.model.Question;
import com.paramount.bed.data.model.QuestionGeneralModel;
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdvertisementResponse {
    public QuestionGeneralModel getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(QuestionGeneralModel questionnaire) {
        this.questionnaire = questionnaire;
    }

    public AdvertiseModel getAdvertisement() {
        return advertisement;
    }

    public void setAdvertisement(AdvertiseModel advertisement) {
        this.advertisement = advertisement;
    }

    @JsonProperty("questionnaire")
    public QuestionGeneralModel questionnaire;

    @JsonProperty("advertisement")
    public AdvertiseModel advertisement;
}
