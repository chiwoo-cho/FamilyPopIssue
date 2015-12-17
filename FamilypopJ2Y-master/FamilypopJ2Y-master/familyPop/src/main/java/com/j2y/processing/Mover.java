package com.j2y.processing;

import shiffman.box2d.*;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

import processing.core.PApplet;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// Mover
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class Mover 
{
	// We need to keep track of a Body and a radius
	Body body;
    public float _rad;
	int _color;
    public int _colorId;
	Fixture fd;

    Box2DProcessing _box2d;

    public boolean _isMoving;   // 어트랙터로 움직일지 여부
    public int _start_time;
    public int _end_time;

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public boolean CreateMover(Box2DProcessing box2d, float rad, float x, float y, int tempC, int _colorId)
	{
		try 
		{
            _box2d = box2d;
            _color = tempC;
            _colorId = _colorId;
			_rad = rad;

			// Define a body
			BodyDef bd_ = new BodyDef();
			bd_.type = BodyType.DYNAMIC;

			// Set its position
			bd_.position = _box2d.coordPixelsToWorld(x, y);
			body = _box2d.world.createBody(bd_);
			
			// Make the body's shape a circle
			CircleShape cs_ = new CircleShape();
			cs_.m_radius = _box2d.scalarPixelsToWorld(this._rad);
	  
			// Define a fixture
			FixtureDef fd_ = new FixtureDef();
			fd_.shape = cs_;
		  
			fd_.density = 0.0001f;
			fd_.friction = 1;
			fd_.restitution = 0.0000001f;

			fd = body.createFixture(fd_);
			body.setLinearVelocity(new Vec2(0, 0));
			body.setAngularVelocity(0);
            _isMoving = false;
            body.setActive(false);
        }
		catch (NullPointerException e) 
		{
			return false;
		}
	  
		return true;
	}

	public void DestroyMover()
	{
		body.destroyFixture(fd);
	}

    public void StartMover(int record_end_time)
    {
        _isMoving = true;
        body.setActive(true);

        _end_time = record_end_time;
    }
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void PlusMoverRadius(float rad)
	{
        if(null == _box2d || null == body)
            return;

        if(_rad > 150f)
            return;
        _rad += rad;
        Fixture ft = body.getFixtureList();
        if (ft.getShape() != null)
            ft.getShape().m_radius += _box2d.scalarPixelsToWorld(rad);
	}
	  
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void applyForce(Vec2 _v, Vec2 _attPos)
	{
		body.applyForce(_v, body.getWorldCenter());
	}

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public Vec2 GetPosition()
    {
        return _box2d.getBodyPixelCoord(body);
    }

	//------------------------------------------------------------------------------------------------------------------------------------------------------
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
        _pApplet.ellipse(0, 0, _rad * 2, _rad * 2);
	    
	    // Let's add a line so we can see the rotation
	    // _pApplet.line(0, 0, _rad, 0);
	    _pApplet.popMatrix();
	  }
	}
