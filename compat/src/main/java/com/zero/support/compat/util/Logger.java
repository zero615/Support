/*
 * Copyright (C) 2005-2017 Qihoo 360 Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.zero.support.compat.util;

import android.util.Log;


public abstract class Logger {
    public static final String TAG = "vp";
    public static final boolean LOG = true;
    private static final String TAG_PREFIX = TAG + ".";
    private static final int MAX_TAG_LENGTH = 23;
    private static final int MAX_PREFIXED_TAG_LENGTH = MAX_TAG_LENGTH - TAG_PREFIX.length();
    private static Logger sLogger;

    public Logger(int loggingLevel) {
    }

    public static int v(String tag, String msg) {
        if (LOG) {
            return Log.v(TAG_PREFIX + tag, msg);
        }
        return -1;
    }

    public static String tagWithPrefix(String tag) {
        int length = tag.length();
        StringBuilder withPrefix = new StringBuilder(MAX_TAG_LENGTH);
        withPrefix.append(TAG_PREFIX);
        if (length >= MAX_PREFIXED_TAG_LENGTH) {
            withPrefix.append(tag.substring(0, MAX_PREFIXED_TAG_LENGTH));
        } else {
            withPrefix.append(tag);
        }
        return withPrefix.toString();
    }

    public static synchronized Logger get() {
        if (sLogger == null) {
            sLogger = new LogcatLogger(Log.DEBUG);
        }
        return sLogger;
    }

    public static synchronized void setLogger(Logger logger) {
        sLogger = logger;
    }


    public static int v(String tag, String msg, Throwable tr) {
        if (LOG) {
            return Log.v(TAG_PREFIX + tag, msg, tr);
        }
        return -1;
    }

    public static int d(String tag, String msg) {
        if (LOG) {
            return Log.d(TAG_PREFIX + tag, msg);
        }
        return -1;
    }


    public static int d(String tag, String msg, Throwable tr) {
        if (LOG) {
            return Log.d(TAG_PREFIX + tag, msg, tr);
        }
        return -1;
    }

    public static int d(String tag, String msg, Object... objects) {
        return d(tag, String.format(msg, objects));
    }

    public static int i(String tag, String msg) {
        if (LOG) {
            return Log.i(TAG_PREFIX + tag, msg);
        }
        return -1;
    }


    public static int i(String tag, String msg, Throwable tr) {
        if (LOG) {
            return Log.i(TAG_PREFIX + tag, msg, tr);
        }
        return -1;
    }

    public static int w(String tag, String msg) {
        if (LOG) {
            return Log.w(TAG_PREFIX + tag, msg);
        }
        return -1;
    }


    public static int w(String tag, String msg, Throwable tr) {
        if (LOG) {
            return Log.w(TAG_PREFIX + tag, msg, tr);
        }
        return -1;
    }


    public static int w(String tag, Throwable tr) {
        if (LOG) {
            return Log.w(TAG_PREFIX + tag, tr);
        }
        return -1;
    }

    public static int e(String tag, String msg) {
        if (LOG) {
            return Log.e(TAG_PREFIX + tag, msg);
        }
        return -1;
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (LOG) {
            return Log.e(TAG_PREFIX + tag, msg, tr);
        }
        return -1;
    }

    /**
     * Equivalent to Log.v.
     */
    public abstract void verbose(String tag, String message, Throwable... throwables);

    /**
     * Equivalent to Log.d.
     */
    public abstract void debug(String tag, String message, Throwable... throwables);

    /**
     * Equivalent to Log.i.
     */
    public abstract void info(String tag, String message, Throwable... throwables);

    /**
     * Equivalent to Log.w.
     */
    public abstract void warning(String tag, String message, Throwable... throwables);

    /**
     * Equivalent to Log.e.
     */
    public abstract void error(String tag, String message, Throwable... throwables);

    public static class LogcatLogger extends Logger {

        private int mLoggingLevel;

        public LogcatLogger(int loggingLevel) {
            super(loggingLevel);
            mLoggingLevel = loggingLevel;
        }

        @Override
        public void verbose(String tag, String message, Throwable... throwables) {
            if (mLoggingLevel <= Log.VERBOSE) {
                if (throwables != null && throwables.length >= 1) {
                    Log.v(tag, message, throwables[0]);
                } else {
                    Log.v(tag, message);
                }
            }
        }

        @Override
        public void debug(String tag, String message, Throwable... throwables) {
            if (mLoggingLevel <= Log.DEBUG) {
                if (throwables != null && throwables.length >= 1) {
                    Log.d(tag, message, throwables[0]);
                } else {
                    Log.d(tag, message);
                }
            }
        }

        @Override
        public void info(String tag, String message, Throwable... throwables) {
            if (mLoggingLevel <= Log.INFO) {
                if (throwables != null && throwables.length >= 1) {
                    Log.i(tag, message, throwables[0]);
                } else {
                    Log.i(tag, message);
                }
            }
        }

        @Override
        public void warning(String tag, String message, Throwable... throwables) {
            if (mLoggingLevel <= Log.WARN) {
                if (throwables != null && throwables.length >= 1) {
                    Log.w(tag, message, throwables[0]);
                } else {
                    Log.w(tag, message);
                }
            }
        }

        @Override
        public void error(String tag, String message, Throwable... throwables) {
            if (mLoggingLevel <= Log.ERROR) {
                if (throwables != null && throwables.length >= 1) {
                    Log.e(tag, message, throwables[0]);
                } else {
                    Log.e(tag, message);
                }
            }
        }
    }
}
