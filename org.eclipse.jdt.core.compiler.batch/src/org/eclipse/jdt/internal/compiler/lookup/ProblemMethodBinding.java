/*******************************************************************************
 * Copyright (c) 2000, 2025 IBM Corporation and others.
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
 *     Stephan Herrmann - Contribution for
 *							Bug 400874 - [1.8][compiler] Inference infrastructure should evolve to meet JLS8 18.x (Part G of JSR335 spec)
 *							Bug 433478 - [compiler][null] NPE in ReferenceBinding.isCompatibleWith
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.List;

public class ProblemMethodBinding extends MethodBinding {

	private final int problemReason;
	public MethodBinding closestMatch; // TODO (philippe) should rename into #alternateMatch
	public InferenceContext18 inferenceContext; // inference context may help to coordinate error reporting
	public TypeBinding missingType;

public ProblemMethodBinding(char[] selector, TypeBinding[] args, int problemReason) {
	this.selector = selector;
	this.parameters = (args == null || args.length == 0) ? Binding.NO_PARAMETERS : args;
	this.problemReason = problemReason;
	this.thrownExceptions = Binding.NO_EXCEPTIONS;
}
public ProblemMethodBinding(char[] selector, TypeBinding[] args, ReferenceBinding declaringClass, int problemReason) {
	this.selector = selector;
	this.parameters = (args == null || args.length == 0) ? Binding.NO_PARAMETERS : args;
	this.declaringClass = declaringClass;
	this.problemReason = problemReason;
	this.thrownExceptions = Binding.NO_EXCEPTIONS;
}
public ProblemMethodBinding(MethodBinding closestMatch, char[] selector, TypeBinding[] args, int problemReason) {
	this(selector, args, problemReason);
	this.closestMatch = closestMatch;
	if (problemReason == ProblemReasons.Ambiguous) {
		if (closestMatch instanceof PolyParameterizedGenericMethodBinding poly)
			poly.hasOverloads = true;
	} else {
		if (closestMatch != null) {
			this.declaringClass = closestMatch.declaringClass;
			this.returnType = closestMatch.returnType;
			if (problemReason == ProblemReasons.InvocationTypeInferenceFailure || problemReason == ProblemReasons.ContradictoryNullAnnotations) {
				this.thrownExceptions = closestMatch.thrownExceptions;
				this.typeVariables = closestMatch.typeVariables;
				this.modifiers = closestMatch.modifiers;
				this.tagBits = closestMatch.tagBits;
			}
		}
	}
}
@Override
public MethodBinding computeSubstitutedMethod(MethodBinding method, LookupEnvironment env) {
	return this.closestMatch == null ? this : this.closestMatch.computeSubstitutedMethod(method, env);
}
@Override
public MethodBinding findOriginalInheritedMethod(MethodBinding inheritedMethod) {
	return this.closestMatch == null ? this : this.closestMatch.findOriginalInheritedMethod(inheritedMethod);
}
@Override
public MethodBinding genericMethod() {
	return this.closestMatch == null ? this : this.closestMatch.genericMethod();
}
@Override
public MethodBinding original() {
	return this.closestMatch == null ? this : this.closestMatch.original();
}
@Override
public MethodBinding shallowOriginal() {
	return this.closestMatch == null ? this : this.closestMatch.shallowOriginal();
}
@Override
public boolean hasSubstitutedParameters() {
	if (this.closestMatch != null)
		return this.closestMatch.hasSubstitutedParameters();
	return false;
}
@Override
public boolean isParameterizedGeneric() {
	return this.closestMatch instanceof ParameterizedGenericMethodBinding;
}
/** API
 * Answer the problem id associated with the receiver.
 * NoError if the receiver is a valid binding.
 */
@Override
public final int problemId() {
	return this.problemReason;
}
@Override
public List<TypeBinding> collectMissingTypes(List<TypeBinding> missingTypes, boolean considerReturnType) {
	if (this.closestMatch != null)
		return this.closestMatch.collectMissingTypes(missingTypes, considerReturnType);
	return super.collectMissingTypes(missingTypes, considerReturnType);
}
}
