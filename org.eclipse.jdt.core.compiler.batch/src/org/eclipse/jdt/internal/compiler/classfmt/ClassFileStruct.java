/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.classfmt;

abstract public class ClassFileStruct {
	byte[] reference;
	int[] constantPoolOffsets;
	final int structOffset;
public ClassFileStruct(byte[] classFileBytes, int[] offsets, int offset) {
	this.reference = classFileBytes;
	this.constantPoolOffsets = offsets;
	this.structOffset = offset;
}
public double doubleAt(int relativeOffset) {
	return (Double.longBitsToDouble(i8At(relativeOffset)));
}
public float floatAt(int relativeOffset) {
	return (Float.intBitsToFloat(i4At(relativeOffset)));
}
public int i4At(int relativeOffset) {
	int position = relativeOffset + this.structOffset;
	return ((this.reference[position++] & 0xFF) << 24) | ((this.reference[position++] & 0xFF) << 16) | ((this.reference[position++] & 0xFF) << 8) + (this.reference[position] & 0xFF);
}
public long i8At(int relativeOffset) {
	int position = relativeOffset + this.structOffset;
	return (((long) (this.reference[position++] & 0xFF)) << 56)
					| (((long) (this.reference[position++] & 0xFF)) << 48)
					| (((long) (this.reference[position++] & 0xFF)) << 40)
					| (((long) (this.reference[position++] & 0xFF)) << 32)
					| (((long) (this.reference[position++] & 0xFF)) << 24)
					| (((long) (this.reference[position++] & 0xFF)) << 16)
					| (((long) (this.reference[position++] & 0xFF)) << 8)
					| (this.reference[position++] & 0xFF);
}
protected void reset() {
	this.reference = null;
	this.constantPoolOffsets = null;
}
public int u1At(int relativeOffset) {
	return (this.reference[relativeOffset + this.structOffset] & 0xFF);
}
public int u2At(int relativeOffset) {
	int position = relativeOffset + this.structOffset;
	return ((this.reference[position++] & 0xFF) << 8) | (this.reference[position] & 0xFF);
}

/**
 * Returns an 32 bit integer created of 4 bytes. The return value should be treated as an <strong>unsigned</strong> integer! i.e. if the caller does math with
 * the result he should use the unsigned methods from Integer like {@link java.lang.Integer#toUnsignedLong(int)} or
 * {@link java.lang.Integer#compareUnsigned(int, int)}. Note that addition with the + operator is safe to use.
 * @param relativeOffset unsigned integer
 * @return unsigned integer
 */
public int u4At(int relativeOffset) {
	int position = relativeOffset + this.structOffset;
	return (((this.reference[position++] & 0xFF) << 24) | ((this.reference[position++] & 0xFF) << 16) | ((this.reference[position++] & 0xFF) << 8) | (this.reference[position] & 0xFF));
}
public char[] utf8At(int relativeOffset, int bytesAvailable) {
	int length = bytesAvailable;
	char outputBuf[] = new char[bytesAvailable];
	int outputPos = 0;
	int readOffset = this.structOffset + relativeOffset;

	while (length != 0) {
		int x = this.reference[readOffset++] & 0xFF;
		length--;
		if ((0x80 & x) != 0) {
			if ((x & 0x20) != 0) {
				length-=2;
				x = ((x & 0xF) << 12) | ((this.reference[readOffset++] & 0x3F) << 6) | (this.reference[readOffset++] & 0x3F);
			} else {
				length--;
				x = ((x & 0x1F) << 6) | (this.reference[readOffset++] & 0x3F);
			}
		}
		outputBuf[outputPos++] = (char) x;
	}

	if (outputPos != bytesAvailable) {
		System.arraycopy(outputBuf, 0, (outputBuf = new char[outputPos]), 0, outputPos);
	}
	return outputBuf;
}
}
