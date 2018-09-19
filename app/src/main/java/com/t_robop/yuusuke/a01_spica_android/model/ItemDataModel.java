package com.t_robop.yuusuke.a01_spica_android.model;

import java.io.Serializable;

public class ItemDataModel implements Serializable {

    private int rightSpeed;
    private int leftSpeed;
    private int time;
    private String orderName;
    private BlockState blockState;
    private int loopCount;

    public enum BlockState {
        FORWARD,
        BACK,
        LEFT,
        RIGHT,
        FOR_START,
        FOR_END
    }

    public ItemDataModel(){}
    public ItemDataModel(String orderName, int rightSpeed, int leftSpeed, int time, BlockState blockState, int loopCount){
        setOrderName(orderName);
        setRightSpeed(rightSpeed);
        setLeftSpeed(leftSpeed);
        setTime(time);
        setBlockState(blockState);
        setLoopCount(loopCount);
    }

    public ItemDataModel(String orderName, BlockState blockState, int loopCount){
        setOrderName(orderName);
        setBlockState(blockState);
        setLoopCount(loopCount);
    }

    public int getRightSpeed(){
        return rightSpeed;
    }
    public int getLeftSpeed(){
        return leftSpeed;
    }
    public int getTime(){
        return time;
    }
    public String getOrderName() {
        return orderName;
    }
    public BlockState  getBlockState(){
        return blockState;
    }
    public int getLoopCount(){
        return loopCount;
    }

    void setRightSpeed(int rightSpeed){
        if (rightSpeed > 255) {
            this.rightSpeed = 255;
        } else if (rightSpeed < 0) {
            this.rightSpeed = 0;
        }
        this.rightSpeed = rightSpeed;
    }
    void setLeftSpeed(int leftSpeed){
        if (leftSpeed > 255) {
            this.leftSpeed = 255;
        } else if (leftSpeed < 0) {
            this.leftSpeed = 0;
        }
        this.leftSpeed = leftSpeed;
    }
    void setTime(int time){
        if (time < 1) {
            this.time = 1;
        }
        this.time = time;
    }
    void setOrderName(String orderName){
        this.orderName = orderName;
    }
    void setBlockState(BlockState blockState){
        this.blockState = blockState;
    }
    void setLoopCount(int loopCount){
        if(loopCount < 0){
            this.loopCount = 0;
        }
        this.loopCount = loopCount;
    }


}