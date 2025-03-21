package kr.hyfata.najoan.async.filecopy;

public enum FileCopyStatus {
    COPYING("복사 중"),
    COMPLETE("완료됨"),
    FAILED("실패");

    private final String name;

    FileCopyStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
