/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

@SuppressWarnings("javadoc")
public class RedstoneRepeaterModel {
	private static Quad[] north = {
		// front
		new Quad(new Vector3d(1, 0, 0), new Vector3d(0, 0, 0),
				new Vector3d(1, .125, 0), new Vector4d(1, 0, 0, .125)),

		// back
		new Quad(new Vector3d(0, 0, 1), new Vector3d(1, 0, 1),
				new Vector3d(0, .125, 1), new Vector4d(0, 1, 0, .125)),

		// right
		new Quad(new Vector3d(0, 0, 0), new Vector3d(0, 0, 1),
				new Vector3d(0, .125, 0), new Vector4d(0, 1, 0, .125)),

		// left
		new Quad(new Vector3d(1, 0, 1), new Vector3d(1, 0, 0),
				new Vector3d(1, .125, 1), new Vector4d(1, 0, 0, .125)),

		// top
		new Quad(new Vector3d(1, .125, 0), new Vector3d(0, .125, 0),
				new Vector3d(1, .125, 1), new Vector4d(1, 0, 1, 0)),
	};

	private static Quad[] torch = {
		new Quad(new Vector3d(.75, 2/16., .4375), new Vector3d(.25, 2/16., .4375),
				new Vector3d(.75, 14/16., .4375), new Vector4d(.75, .25, 4/16., 1)),

		new Quad(new Vector3d(.25, 2/16., .5625), new Vector3d(.75, 2/16., .5625),
				new Vector3d(.25, 14/16., .5625), new Vector4d(.25, .75, 4/16., 1)),

		new Quad(new Vector3d(.4375, 2/16., .25), new Vector3d(.4375, 2/16., .75),
				new Vector3d(.4375, 14/16., .25), new Vector4d(.25, .75, 4/16., 1)),

		new Quad(new Vector3d(.5625, 2/16., .75), new Vector3d(.5625, 2/16., .25),
				new Vector3d(.5625, 14/16., .75), new Vector4d(.75, .25, 4/16., 1)),

		// top
		new Quad(new Vector3d(.4375, 8/16., .5625), new Vector3d(.5625, 8/16., .5625),
				new Vector3d(.4375, 8/16., .4375), new Vector4d(.4375, .5625, .5, .625)),
	};

	private static Quad[][] torch1 = new Quad[4][];
	private static Quad[][][] torch2 = new Quad[4][4][];

	private static final Quad[][] rot = new Quad[4][];

	private static final Texture[] tex = {
		Texture.redstoneRepeaterOff,
		Texture.redstoneRepeaterOn,
	};

	private static final Texture[] torchTex = {
		Texture.redstoneTorchOff,
		Texture.redstoneTorchOn,
	};

	static {
		rot[0] = north;
		rot[1] = Model.rotateY(rot[0]);
		rot[2] = Model.rotateY(rot[1]);
		rot[3] = Model.rotateY(rot[2]);

		torch1[0] = Model.translate(torch, 0, 0, -5/16.);
		torch1[1] = Model.rotateY(torch1[0]);
		torch1[2] = Model.rotateY(torch1[1]);
		torch1[3] = Model.rotateY(torch1[2]);

		torch2[0][0] = Model.translate(torch, 0, 0, -1/16.);
		torch2[0][1] = Model.rotateY(torch2[0][0]);
		torch2[0][2] = Model.rotateY(torch2[0][1]);
		torch2[0][3] = Model.rotateY(torch2[0][2]);

		torch2[1][0] = Model.translate(torch, 0, 0, 1/16.);
		torch2[1][1] = Model.rotateY(torch2[1][0]);
		torch2[1][2] = Model.rotateY(torch2[1][1]);
		torch2[1][3] = Model.rotateY(torch2[1][2]);

		torch2[2][0] = Model.translate(torch, 0, 0, 3/16.);
		torch2[2][1] = Model.rotateY(torch2[2][0]);
		torch2[2][2] = Model.rotateY(torch2[2][1]);
		torch2[2][3] = Model.rotateY(torch2[2][2]);

		torch2[3][0] = Model.translate(torch, 0, 0, 5/16.);
		torch2[3][1] = Model.rotateY(torch2[3][0]);
		torch2[3][2] = Model.rotateY(torch2[3][1]);
		torch2[3][3] = Model.rotateY(torch2[3][2]);
	}

	public static boolean intersect(Ray ray, int on) {
		boolean hit = false;
		int data = ray.getBlockData();
		int direction = data & 3;
		int delay = (data >> 2) & 3;
		ray.t = Double.POSITIVE_INFINITY;
		for (Quad face: rot[direction]) {
			if (face.intersect(ray)) {
				tex[on].getColor(ray);
				ray.n.set(face.n);
				ray.t = ray.tNext;
				hit = true;
			}
		}
		for (Quad face: torch1[direction]) {
			if (face.intersect(ray)) {
				float[] color = torchTex[on].getColor(ray.u, ray.v);
				if (color[3] > Ray.EPSILON) {
					ray.color.set(color);
					ray.n.set(face.n);
					ray.t = ray.tNext;
					hit = true;
				}
			}
		}
		for (Quad face: torch2[delay][direction]) {
			if (face.intersect(ray)) {
				float[] color = torchTex[on].getColor(ray.u, ray.v);
				if (color[3] > Ray.EPSILON) {
					ray.color.set(color);
					ray.n.set(face.n);
					ray.t = ray.tNext;
					hit = true;
				}
			}
		}
		if (hit) {
			ray.color.w = 1;
			ray.distance += ray.t;
			ray.o.scaleAdd(ray.t, ray.d);
		}
		return hit;
	}
}
