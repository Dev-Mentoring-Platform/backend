<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
package com.project.mentoridge.modules.purchase.controller.response;

import com.project.mentoridge.modules.lecture.controller.response.SimpleLectureResponse;
import com.project.mentoridge.modules.purchase.vo.Pick;

public class PickWithSimpleLectureResponse {

    private Long pickId;
    private SimpleLectureResponse lecture;

    public PickWithSimpleLectureResponse(Pick pick) {
        this.pickId = pick.getId();
        this.lecture = new SimpleLectureResponse(pick.getLecture(), pick.getLecturePrice());
    }
=======
package com.project.mentoridge.modules.purchase.controller.response;public class PickWithSimpleLectureResponse {
>>>>>>> e3fd6a89e80deff5eb69b442fb807180fe2f2235
=======
package com.project.mentoridge.modules.purchase.controller.response;public class PickWithSimpleLectureResponse {
>>>>>>> e3fd6a8... pick 리스트 API 수정 및 전체 테스트
=======
package com.project.mentoridge.modules.purchase.controller.response;public class PickWithSimpleLectureResponse {
>>>>>>> e3fd6a89e80deff5eb69b442fb807180fe2f2235
}
