//
// Created by Admin on 2021/7/18.
//

#ifndef OPENGLES_RECORDBUFFER_H
#define OPENGLES_RECORDBUFFER_H

public:
    short **buffer;
    int index = -1;

    RecordBuffer(int bufferSize);
    ~RecordBuffer();

    short *getRecordBuffer();

    short *getNowBuffer();
};


#endif //OPENGLES_RECORDBUFFER_H
