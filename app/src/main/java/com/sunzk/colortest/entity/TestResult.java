package com.sunzk.colortest.entity;

import java.util.Date;

public class TestResult {
	
	private int id;
	private String date;
	private float questionH;
	private float questionS;
	private float questionB;
	private float answerH;
	private float answerS;
	private float answerB;

	public TestResult() {}

	public TestResult(int id, String date, float questionH, float questionS, float questionB, float answerH, float answerS, float answerB) {
		this.id = id;
		this.date = date;
		this.questionH = questionH;
		this.questionS = questionS;
		this.questionB = questionB;
		this.answerH = answerH;
		this.answerS = answerS;
		this.answerB = answerB;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public float getQuestionH() {
		return questionH;
	}

	public void setQuestionH(float questionH) {
		this.questionH = questionH;
	}

	public float getQuestionS() {
		return questionS;
	}

	public void setQuestionS(float questionS) {
		this.questionS = questionS;
	}

	public float getQuestionB() {
		return questionB;
	}

	public void setQuestionB(float questionB) {
		this.questionB = questionB;
	}

	public float getAnswerH() {
		return answerH;
	}

	public void setAnswerH(float answerH) {
		this.answerH = answerH;
	}

	public float getAnswerS() {
		return answerS;
	}

	public void setAnswerS(float answerS) {
		this.answerS = answerS;
	}

	public float getAnswerB() {
		return answerB;
	}

	public void setAnswerB(float answerB) {
		this.answerB = answerB;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("TestResult{");
		sb.append("id=").append(id);
		sb.append(", date='").append(date).append('\'');
		sb.append(", questionH=").append(questionH);
		sb.append(", questionS=").append(questionS);
		sb.append(", questionB=").append(questionB);
		sb.append(", answerH=").append(answerH);
		sb.append(", answerS=").append(answerS);
		sb.append(", answerB=").append(answerB);
		sb.append('}');
		return sb.toString();
	}
}
