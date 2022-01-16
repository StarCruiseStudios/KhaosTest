/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.verification

/**
 * An exception that indicates a test verification has failed.
 *
 * A test [message] is provided to add context to the exception thrown and
 * a [cause] is specified to indicate what the verification failure is.
 */
class VerificationException(message: String?, cause: Throwable) : Exception(message, cause)
