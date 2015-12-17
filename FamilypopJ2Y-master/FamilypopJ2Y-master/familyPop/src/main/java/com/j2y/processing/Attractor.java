package com.j2y.processing;

import shiffman.box2d.*;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

import processing.core.PApplet;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// Attractor
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class Attractor 
{
	// We need to keep track of a Body and a radius
	Body body;
	float rad;
	int c;

    Box2DProcessing _box2d;

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public Attractor(Box2DProcessing box2d, float _rad, float _x, float _y, int tempC)
	{
        _box2d = box2d;
		c = tempC;
		rad = _rad;
    
		// Define a body
		BodyDef bd_ = new BodyDef();
		bd_.type = BodyType.STATIC;
		// Set its position
		bd_.position = _box2d.coordPixelsToWorld(_x, _y);
		body = _box2d.world.createBody(bd_);
    
		// Make the body's shape a circle
		CircleShape cs_ = new CircleShape();
		cs_.m_radius = _box2d.scalarPixelsToWorld(rad);
   
		body.createFixture(cs_, 1);
	}

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public Vec2 GetPosition()
    {
        return _box2d.getBodyPixelCoord(body);
    }


	//------------------------------------------------------------------------------------------------------------------------------------------------------
	// Formula for gravitational attraction
	// We are computing this in "world" coordinates
	// No need to convert to pixels and back
	public Vec2 attract(Mover _mover) 
	{
    
		float g_ = 1000; // Strength of force
		// clone() makes us a copy
		Vec2 pos_ = body.getWorldCenter();
		Vec2 pos_mover_ = _mover.body.getWorldCenter();
		// Vector pointing from mover to attractor
		Vec2 force_ = pos_.sub(pos_mover_);
		float dist_ = force_.length();
    
		// Keep force within bounds
		dist_ = PApplet.constrain(dist_, 1, 2);
		force_.normalize();
		// Note the attractor's mass is 0 because it's fixed so can't use that
		float strength_ = (g_ * 1 * _mover.body.m_mass) / (dist_ * dist_); // Calculate
                                      // gravitional
                                      // force
                                      // magnitude
		force_.mulLocal(strength_); // Get force vector --> magnitude *
		// direction
		return force_;
	}

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public Vec2 GetAttractorPos(Box2DProcessing _box2d)
    {
        return _box2d.getBodyPixelCoord(body);
    }

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void display(PApplet _pApplet, Box2DProcessing _box2d) 
	{
		// We look at each body and get its screen position
		Vec2 pos = _box2d.getBodyPixelCoord(body);
		// Get its angle of rotation
		float angle_ = body.getAngle();
		_pApplet.pushMatrix();
		_pApplet.translate(pos.x, pos.y);
		_pApplet.rotate(angle_);
		_pApplet.fill(c);
		//_pApplet.stroke(0);
		_pApplet.noStroke();
		_pApplet.strokeWeight(1);
		_pApplet.ellipse(0, 0, rad * 2, rad * 2);
		_pApplet.popMatrix();
	}
}
