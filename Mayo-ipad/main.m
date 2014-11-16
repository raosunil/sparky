//
//  main.m
//  Mayo-ipad
//
//  Created by Rishabh Srivastava on 08/10/14.
//  Copyright (c) 2014 rishabh srivastava. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "AppDelegate.h"

extern NSMutableDictionary *userMap;
extern NSMutableDictionary *patientMap;

int main(int argc, char * argv[]) {
    @autoreleasepool {
        userMap = [[NSMutableDictionary alloc] init];
        patientMap = [[NSMutableDictionary alloc] init];
        return UIApplicationMain(argc, argv, nil, NSStringFromClass([AppDelegate class]));
    }
}
