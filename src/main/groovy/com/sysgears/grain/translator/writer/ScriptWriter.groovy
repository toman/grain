/*
 * Copyright (c) 2013 SysGears, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sysgears.grain.translator.writer

import javax.inject.Named

/**
 * Builds a sequence of out.write() statements that have arbitrary groovy code between them.
 */
@Named
class ScriptWriter {

    /**
     * Maximum allowed line length 
     */
    private static final def MAX_LINE_LENGTH = 30000
    
    /** String writer that contains resulting groovy code */
    private StringWriter sw

    /** The type of the statement being written */
    private StatementType curStatement

    /** Current line length */
    private int lineLength

    /**
     * Creates an instance of output composer. 
     */
    public ScriptWriter() {
        sw = new StringWriter()
        curStatement = StatementType.PLAIN_CODE
        lineLength = 0
    }

    /**
     * Writes text wrapped by the specified statement. 
     * 
     * @param statement a type of statement to wrap the text in
     * @param text a text to write
     */
    public void write(String text, StatementType statement = StatementType.GSTRING_WRITE) {
        if (text.length() > 0) {
            def maxChars = MAX_LINE_LENGTH - statement.closeStr.trim().length() - lineLength
            if (curStatement != statement) {
                maxChars -= curStatement.closeStr.trim().length() + statement.openStr.length()
            }
            if (text.length() > maxChars && statement != StatementType.PLAIN_CODE) {
                write(text.substring(0, maxChars), statement)
                write(text.substring(maxChars), statement)
            } else {
                if (curStatement != statement) {
                    sw.write(curStatement.closeStr)
                    sw.write(statement.openStr)
                    lineLength = statement.openStr.length()
                    curStatement = statement
                }
                sw.write(text)
                lineLength += text.length()
                if (lineLength + statement.closeStr.length() >= MAX_LINE_LENGTH) {
                    sw.write(statement.closeStr)
                    sw.write(statement.openStr)
                    lineLength = statement.openStr.length()
                }
            }
        }
    }
    
    /**
     * Returns sequence of out.write statements intermixed with groovy code as a string 
     * 
      * @return sequence of out.write statements intermixed with groovy code as a string
     */
    public String toString() {
        if (curStatement != StatementType.PLAIN_CODE) {
            sw.write(curStatement.closeStr)
            curStatement = StatementType.PLAIN_CODE
        }
        sw.toString()
    }
}