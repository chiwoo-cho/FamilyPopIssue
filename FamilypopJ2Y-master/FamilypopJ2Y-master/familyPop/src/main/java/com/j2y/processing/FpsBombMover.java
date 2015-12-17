package com.j2y.processing;

import org.jbox2d.common.Vec2;

import processing.core.PApplet;
import processing.core.PImage;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpsBombMover
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpsBombMover extends Mover
{
    // We need to keep track of a Body and a radius
    private PImage _bombImage = null;
    private PImage _explosionImage = null;
    private PImage _disPlayImage = null;

    public FpsBombMover(PImage bombImage, PImage explosionImage)
    {
        _disPlayImage = _bombImage = bombImage;
        _explosionImage = explosionImage;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void applyForce(Vec2 _v, Vec2 _attPos)
    {
        super.applyForce(_v, _attPos);

        Vec2 vTaget = _attPos;
        Vec2 vCurBody = _box2d.getBodyPixelCoord(body);

        Vec2 vLength = new Vec2(vTaget.x -  vCurBody.x, vTaget.y -  vCurBody.y);

        if(Math.sqrt(vLength.x * vLength.x + vLength.y * vLength.y) < 90)
        {
            _disPlayImage = _explosionImage;
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void display(PApplet _pApplet)
    {
        // We look at each body and get its screen position
        Vec2 pos = _box2d.getBodyPixelCoord(body);

        // Get its angle of rotation
        float angle_ = body.getAngle();
        _pApplet.pushMatrix();
        _pApplet.translate(pos.x, pos.y);
        _pApplet.rotate(angle_);
        // _pApplet.fill(150);
        _pApplet.noFill();
        _pApplet.stroke(_color);
        _pApplet.strokeWeight(3);

        if(_disPlayImage != null)
            _pApplet.image(_disPlayImage, -_bombImage.width / 2, -_disPlayImage.height / 2);
        else
            _pApplet.ellipse(0, 0, _rad * 2, _rad * 2);

        // Let's add a line so we can see the rotation
        // _pApplet.line(0, 0, _rad, 0);
        _pApplet.popMatrix();
    }
}
