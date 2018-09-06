union () {
  translate ([213, 0, -12]) {
    cube ([12, 6, 30], center=true);
  }
  union () {
    translate ([182, 0, -12]) {
      cube ([12, 6, 30], center=true);
    }
    union () {
      translate ([151, 0, -12]) {
        cube ([12, 6, 30], center=true);
      }
      union () {
        translate ([120, 0, -12]) {
          cube ([12, 6, 30], center=true);
        }
        union () {
          translate ([89, 0, -12]) {
            cube ([12, 6, 30], center=true);
          }
          union () {
            translate ([58, 0, -12]) {
              cube ([12, 6, 30], center=true);
            }
            union () {
              translate ([27, 0, -12]) {
                cube ([12, 6, 30], center=true);
              }
              union () {
                translate ([131, 0, -70]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=50, h=222, r=43, center=true);
                  }
                }
                translate ([242, 0, 0]) {
                  union () {
                    translate ([10, 0, -70]) {
                      rotate (a=90.0, v=[0, 1, 0]) {
                        cylinder ($fn=50, h=20, r=90, center=true);
                      }
                    }
                    translate ([10, 0, 65]) {
                      cube ([20, 90, 180], center=true);
                    }
                  }
                }
                translate ([121, 0, 120]) {
                  rotate (a=90.0, v=[0, 1, 0]) {
                    cylinder ($fn=50, h=242, r=10, center=true);
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
